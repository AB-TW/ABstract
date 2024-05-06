package cn.dpc.ab.domain.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FilterConditions represents a collection of filterCondition template.
 */
public interface FilterConditions {

    /**
     * Add a new experiment.
     *
     * @param filterConditionTemplate the filterConditionTemplate to add
     * @return a Mono that completes when the filterConditionTemplate has been added
     */
    Mono<FilterConditionTemplate> add(FilterConditionTemplate filterConditionTemplate);

    /**
     * Get all experiments.
     *
     * @return a Flux that emits all filterConditionTemplate
     */
    Flux<FilterConditionTemplate> getAll();

    Mono<Void> update(String templateId, FilterConditionTemplate filterConditionTemplate);

    Mono<Void> delete(String templateId);

}
