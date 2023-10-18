package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import lombok.RequiredArgsConstructor;
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
    public Flux<Experiment> subCollection(FilterParams params, int from, int to) {
        return null;
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return null;
    }

    @Override
    public Mono<Experiment> add(Experiment experiment) {
        return null;
    }

    @Override
    public Mono<Experiment> getExperiment(Experiment.ExperimentId id) {
        return experimentDbRepository.findById(id.id()).map(ExperimentDb::toExperiment);
    }

    @Override
    public Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description) {
        return null;
    }

    @Override
    public Mono<Void> publish(Experiment.ExperimentId id) {
        return null;
    }

    @Override
    public Mono<Void> pause(Experiment.ExperimentId id) {
        return null;
    }

    @Override
    public Mono<Void> stop(Experiment.ExperimentId id) {
        return null;
    }

    @Override
    public Flux<Experiment> getByFeatureKey(String featureKey) {
        return experimentDbRepository.findByFeatureKey(featureKey, Sort.by("description.updatedAt").descending())
                .map(ExperimentDb::toExperiment);
    }
}
