package cn.dpc.ab.domain.experiment;

import reactor.core.publisher.Flux;

/**
 * Experiments represents a collection of experiments.
 */
public interface Experiments {
    Flux<Experiment> getAllExperiments();
}
