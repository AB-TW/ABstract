package cn.dpc.ab.persistent.filtercondition;

import cn.dpc.ab.domain.core.FilterConditionTemplate;
import cn.dpc.ab.domain.core.FilterConditions;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionTemplateDb;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

/**
 * Implement filterCondition.
 */
@Component
@RequiredArgsConstructor
public class FilterConditionsImpl implements FilterConditions {

    private final FilterConditionDbRepository repository;

    @Override
    public Mono<FilterConditionTemplate> add(FilterConditionTemplate filterConditionTemplate) {
        filterConditionTemplate.setCreatedAt(LocalDateTime.now());
        filterConditionTemplate.setUpdatedAt(LocalDateTime.now());
        return repository.save(FilterConditionTemplateDb.from(filterConditionTemplate))
                .map(FilterConditionTemplateDb::to);
    }

    @Override
    public Flux<FilterConditionTemplate> getAll() {
        return repository.findAll().map(FilterConditionTemplateDb::to);
    }

    @Override
    public Mono<Void> update(String templateId, FilterConditionTemplate filterConditionTemplate) {
        return repository.findById(templateId)
                .switchIfEmpty(Mono.error(new IllegalStateException("FilterCondition template not found: " + templateId)))
                .flatMap(repository::save)
                .publishOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Void> delete(String templateId) {
        return repository.deleteById(templateId);
    }

}
