package cn.dpc.ab.persistent.filtercondition;

import cn.dpc.ab.domain.core.FilterConditionTemplate;
import cn.dpc.ab.domain.core.FilterConditions;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionTemplateDb;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
