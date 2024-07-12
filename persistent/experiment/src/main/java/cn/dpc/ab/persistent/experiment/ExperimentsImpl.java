package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static cn.dpc.ab.domain.experiment.Experiment.Status.DynamicStatus.DRAFT;

/**
 * Implement experiments.
 */
@Component
@RequiredArgsConstructor
public class ExperimentsImpl implements Experiments {

    private final ExperimentDbRepository experimentDbRepository;
    private final AssignmentDbRepository assignmentDbRepository;
    private final LocalCacheManager localCacheManager;


    @Override
    public Flux<Experiment> getAll(FilterParams params) {
        return null;
    }

    @Override
    public Flux<Experiment> subCollection(FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        pageSize = pageSize < 0 ? 10 : pageSize;
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("updatedAt").descending());

        return experimentDbRepository.findByFilterParams(ExperimentDbRepository.FilterParams.from(params), pageRequest)
                .map(ExperimentDb::toExperiment);
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return experimentDbRepository.countByFilterParams(ExperimentDbRepository.FilterParams.from(params));
    }

    @Override
    public Mono<Experiment> add(Experiment experiment) {
        return experimentDbRepository.save(ExperimentDb.from(experiment))
                .map(ExperimentDb::toExperiment)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(item ->
                        clearCache(item.getDescription().featureKey()));
    }

    @Override
    public Mono<Experiment> get(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id()).map(experimentDb -> {
            Experiment experiment = experimentDb.toExperiment();
            experiment.setAssignments(new AssignmentsImpl(assignmentDbRepository, experiment));
            return experiment;
        });
    }

    @Override
    public Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> experimentDbRepository.save(experimentDb
                        .updateDescription(experimentDb.toExperiment().update(description).getDescription())))
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(db -> clearCache(db.getFeatureKey()))
                .then();
    }

    @Override
    public Mono<Void> publish(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.PUBLISHED);
                    return experimentDbRepository.save(experimentDb);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(db -> clearCache(db.getFeatureKey()))
                .then();
    }

    @Override
    public Mono<Void> pause(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.PAUSED);
                    return experimentDbRepository.save(experimentDb);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(db -> clearCache(db.getFeatureKey()))
                .then();

    }

    @Override
    public Mono<Void> stop(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.STOPPED);
                    return experimentDbRepository.save(experimentDb);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(db -> clearCache(db.getFeatureKey()))
                .then();
    }

    @Override
    public Mono<Experiment> getByFeatureKey(String featureKey) {
        return this.subCollection(new FilterParams(null, featureKey,
                        List.of(Experiment.Status.DynamicStatus.NOT_BEGIN,
                                Experiment.Status.DynamicStatus.COLLECTING,
                                Experiment.Status.DynamicStatus.FINISH_COLLECT,
                                Experiment.Status.DynamicStatus.PAUSED
                        )), 0, 1)
                .next();
    }

    @Override
    public Mono<Void> delete(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .flatMap(experimentDb -> {
                    if (experimentDb.getStatus().getStatus().equals(DRAFT)) {
                        return experimentDbRepository.deleteById(id.id())
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(db -> clearCache(experimentDb.getFeatureKey()));
                    }
                    return Mono.error(new IllegalArgumentException("Only draft experiment can be deleted!"));
                })
                .then();
    }

    private void clearCache(String featureKey) {
        localCacheManager.clearCache(LocalCacheManager.EXPERIMENT_KEY_PREFIX
                        + featureKey)
                .subscribe();
    }
}
