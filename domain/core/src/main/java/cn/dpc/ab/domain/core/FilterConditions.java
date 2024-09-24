package cn.dpc.ab.domain.core;

import lombok.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FilterConditions represents a collection of filterCondition template.
 */
public interface FilterConditions {

    /**
     * FilterParams provides a set of parameters to filter experiments.
     */
    @Builder
    record FilterParams(String key) {
    }

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

    /**
     * Get a sub-collection of filterConditionTemplates.
     *
     * @param params the filter params
     * @param from   the start index of the sub-collection
     * @param to     the end index of the sub-collection
     * @return a Flux that emits a sub-collection of experiments
     */
    Flux<FilterConditionTemplate> subCollection(FilterParams params, long from, long to);

    /**
     * Get the size of a sub-collection of filterConditionTemplates.
     *
     * @param params the filter params
     * @return a Mono that emits the size of a sub-collection of filterConditionTemplates
     */
    Mono<Long> size(FilterParams params);

    Mono<FilterConditionTemplate> getById(String templateId);

    Mono<Void> update(String templateId, FilterConditionTemplate filterConditionTemplate);

    Mono<Void> delete(String templateId);

}
