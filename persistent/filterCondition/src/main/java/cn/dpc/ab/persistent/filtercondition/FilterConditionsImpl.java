package cn.dpc.ab.persistent.filtercondition;

import cn.dpc.ab.domain.core.FilterConditionTemplate;
import cn.dpc.ab.domain.core.FilterConditions;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionDbRepository;
import cn.dpc.ab.persistent.filtercondition.db.FilterConditionTemplateDb;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Flux<FilterConditionTemplate> subCollection(FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        pageSize = pageSize < 0 ? 10 : pageSize;
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("updatedAt").descending());

        return repository.findByFilterParams(params, pageRequest)
                .map(FilterConditionTemplateDb::to);
    }

    @Override
    public Mono<FilterConditionTemplate> getById(String templateId) {
        return repository.findById(templateId)
                .map(FilterConditionTemplateDb::to)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(templateId)));
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return repository.countByFilterParams(params);
    }

    @Override
    public Mono<Void> update(String templateId, FilterConditionTemplate filterConditionTemplate) {
        return repository.findById(templateId)
                .switchIfEmpty(Mono.error(new IllegalStateException("FilterCondition template not found: " + templateId)))
                .flatMap(experimentDb -> repository.save(experimentDb.updateFrom(filterConditionTemplate)))
                .publishOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Void> delete(String templateId) {
        return repository.deleteById(templateId);
    }

}
