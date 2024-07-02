package cn.dpc.ab.domain.experiment;

import lombok.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Experiments represents a collection of experiments.
 */
public interface Experiments {

    /**
     * FilterParams provides a set of parameters to filter experiments.
     */
    @Builder
    record FilterParams(String featureKey, Experiment.Status.DynamicStatus status) {}

    /**
     * Get all experiments.
     *
     * @param params the filter params
     * @return a Flux that emits all experiments
     */
    Flux<Experiment> getAll(FilterParams params);

    /**
     * Get a sub-collection of experiments.
     *
     * @param params the filter params
     * @param from   the start index of the sub-collection
     * @param to     the end index of the sub-collection
     * @return a Flux that emits a sub-collection of experiments
     */
    Flux<Experiment> subCollection(FilterParams params, long from, long to);

    /**
     * Get the size of a sub-collection of experiments.
     *
     * @param params the filter params
     * @return a Mono that emits the size of a sub-collection of experiments
     */
    Mono<Long> size(FilterParams params);

    /**
     * Add a new experiment.
     *
     * @param experiment the experiment to add
     * @return a Mono that completes when the experiment has been added
     */
    Mono<Experiment> add(Experiment experiment);

    /**
     * Get an experiment.
     *
     * @param id the id of the experiment to get
     * @return a Mono that completes when the experiment has been retrieved
     */
    Mono<Experiment> get(Experiment.ExperimentId id);

    /**
     * Update an experiment.
     *
     * @param id          the id of the experiment to update
     * @param description the description of the experiment to update
     * @return a Mono that completes when the experiment has been updated
     */
    Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description);

    /**
     * Publish an experiment.
     *
     * @param id the id of the experiment to publish
     * @return a Mono that completes when the experiment has been published
     */
    Mono<Void> publish(Experiment.ExperimentId id);

    /**
     * Pause an experiment.
     *
     * @param id the id of the experiment to pause
     * @return a Mono that completes when the experiment has been paused
     */
    Mono<Void> pause(Experiment.ExperimentId id);

    /**
     * Stop an experiment.
     *
     * @param id the id of the experiment to stop
     * @return a Mono that completes when the experiment has been stopped
     */
    Mono<Void> stop(Experiment.ExperimentId id);

    Mono<Experiment> getByFeatureKey(String featureKey);

    Mono<Void> delete(Experiment.ExperimentId id);
}
