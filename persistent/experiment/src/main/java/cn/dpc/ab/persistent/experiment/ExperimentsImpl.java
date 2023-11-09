package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implement experiments.
 */
@Component
@RequiredArgsConstructor
public class ExperimentsImpl implements Experiments {

    private final ExperimentDbRepository experimentDbRepository;

    @Override
    public Flux<Experiment> getAllExperiments(FilterParams params) {
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
                .map(ExperimentDb::toExperiment);
    }

    @Override
    public Mono<Experiment> getExperiment(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id()).map(ExperimentDb::toExperiment);
    }

    @Override
    public Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> experimentDbRepository.save(experimentDb
                        .updateDescription(experimentDb.toExperiment().update(description).getDescription())))
                .then();
    }

    @Override
    public Mono<Void> publish(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.PUBLISHED);
                    return experimentDbRepository.save(experimentDb);
                }).then();
    }

    @Override
    public Mono<Void> pause(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.PAUSED);
                    return experimentDbRepository.save(experimentDb);
                }).then();

    }

    @Override
    public Mono<Void> stop(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Experiment not found: " + id.id())))
                .flatMap(experimentDb -> {
                    experimentDb.getStatus().updateStatus(Experiment.Status.StaticStatus.STOPPED);
                    return experimentDbRepository.save(experimentDb);
                }).then();
    }

    @Override
    public Flux<Experiment> getByFeatureKey(String featureKey) {
        return experimentDbRepository.findByFeatureKey(featureKey, Sort.by("description.updatedAt").descending())
                .map(ExperimentDb::toExperiment);
    }
}
