package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.persistent.experiment.db.AssignmentRepository;
import cn.dpc.ab.persistent.experiment.db.CustomerExperimentRepository;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implement experiments.
 */
@Component
@RequiredArgsConstructor
public class ExperimentsImpl implements Experiments {

    private final ExperimentRepository repository;
    private final AssignmentRepository assignmentRepository;
    private final CustomerExperimentRepository customerExperimentRepository;


    /**
     * getAllExperiments returns all experiments.
     *
     * @see Experiments#getAll(FilterParams)
     */
    @Override
    public Flux<Experiment> getAll(FilterParams params) {
        return customerExperimentRepository.findAll(params)
                .map(ExperimentDb::to);
    }


    /**
     * subCollection returns a sub collection of experiments.
     *
     * @see Experiments#subCollection(FilterParams, long, long)
     */
    @Override
    public Flux<Experiment> subCollection(FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        pageSize = pageSize < 0 ? 10 : pageSize;
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("updatedAt").descending());

        return customerExperimentRepository.findAll(params, pageRequest)
                .map(ExperimentDb::to);
    }


    /**
     * size returns the size of experiments.
     *
     * @see Experiments#size(FilterParams)
     */
    @Override
    public Mono<Long> size(FilterParams params) {
        return customerExperimentRepository.count(params);
    }


    /**
     * add adds an experiment to the database.
     *
     * @see Experiments#add(Experiment)
     */
    @Override
    public Mono<Experiment> add(Experiment experiment) {
        return repository.save(ExperimentDb.from(experiment)).map(ExperimentDb::to);
    }


    /**
     * getExperiment retrieves an experiment by id from the database.
     *
     * @see Experiments#get(Experiment.ExperimentId)
     */
    @Override
    public Mono<Experiment> get(Experiment.ExperimentId id) {
        return repository.findById(id.id())
                .map(experimentDb -> {
                    Experiment experiment = experimentDb.to();
                    experiment.setAssignments(new AssignmentsImpl(assignmentRepository, experiment));
                    return experiment;
                });
    }


    /**
     * update updates an experiment.
     *
     * @see Experiments#update(Experiment.ExperimentId, Experiment.Description)
     */
    @Override
    public Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description) {
        return repository.findById(id.id())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.id())))
                .flatMap(db -> {
                    db.updateDescription(db.to().update(description).getDescription());
                    return repository.save(db);
                })
                .then();
    }


    /**
     * publish publishes an experiment.
     *
     * @see Experiments#publish(Experiment.ExperimentId)
     */
    @Override
    public Mono<Void> publish(Experiment.ExperimentId id) {
        return repository.findById(id.id())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.id())))
                .flatMap(db -> {
                    db.updateDescription(db.to().publish().getDescription());
                    return repository.save(db);
                })
                .then();
    }


    /**
     * pause pauses an experiment.
     *
     * @see Experiments#pause(Experiment.ExperimentId)
     */
    @Override
    public Mono<Void> pause(Experiment.ExperimentId id) {
        return repository.findById(id.id())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.id())))
                .flatMap(db -> {
                    db.updateDescription(db.to().pause().getDescription());
                    return repository.save(db);
                })
                .then();
    }


    /**
     * stop stops an experiment.
     *
     * @see Experiments#stop(Experiment.ExperimentId)
     */
    @Override
    public Mono<Void> stop(Experiment.ExperimentId id) {
        return repository.findById(id.id())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.id())))
                .flatMap(db -> {
                    db.updateDescription(db.to().stop().getDescription());
                    return repository.save(db);
                })
                .then();
    }


    /**
     * getByFeatureKey retrieves all experiments by key from the database.
     *
     * @see Experiments#getByFeatureKey(String)
     */
    @Override
    public Mono<Experiment> getByFeatureKey(String featureKey) {
        return this.subCollection(new FilterParams(null, featureKey,
                        List.of(Experiment.Status.DynamicStatus.NOT_BEGIN,
                                Experiment.Status.DynamicStatus.COLLECTING,
                                Experiment.Status.DynamicStatus.FINISH_COLLECT,
                                Experiment.Status.DynamicStatus.PAUSED
                        )), 0, 1)
                .next()
                .map(experiment -> {
                    experiment.setAssignments(new AssignmentsImpl(assignmentRepository, experiment));
                    return experiment;
                });
    }

    @Override
    public Mono<Void> delete(Experiment.ExperimentId id) {
        return repository.findById(id.id())
                .flatMap(experimentDb -> {
                    if (experimentDb.getStaticStatusAsEnum() == Experiment.Status.StaticStatus.DRAFT) {
                        return repository.deleteById(id.id());
                    }
                    return Mono.error(new RecordNotFoundException(id.id()));
                });
    }
}
