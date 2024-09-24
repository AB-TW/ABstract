package cn.dpc.ab.api.filtercondition;

import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.filtercondition.dto.FilterConditionPageQueryRequest;
import cn.dpc.ab.api.filtercondition.dto.FilterConditionTemplateRequest;
import cn.dpc.ab.api.filtercondition.dto.FilterConditionTemplateResponse;
import cn.dpc.ab.domain.core.FilterConditions;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * filterCondition api.
 */
@RestController
@RequestMapping("/filterCondition")
@RequiredArgsConstructor
public class FilterConditionApi {

    private final FilterConditions filterConditions;

    @PostMapping
    Mono<FilterConditionTemplateResponse> add(@RequestBody FilterConditionTemplateRequest filterConditionTemplateRequest) {
        return filterConditions.add(filterConditionTemplateRequest.to())
                .map(FilterConditionTemplateResponse::from);
    }

    @GetMapping
    Mono<PageResponse<FilterConditionTemplateResponse>> getByPage(FilterConditionPageQueryRequest filterConditionPageQueryRequest) {
        FilterConditions.FilterParams filterParams = filterConditionPageQueryRequest.filterParams();
        return filterConditions.subCollection(filterParams, filterConditionPageQueryRequest.getFrom(), filterConditionPageQueryRequest.getTo())
                .map(FilterConditionTemplateResponse::from)
                .collectList().zipWith(filterConditions.size(filterParams))
                .map(tuple -> PageResponse.<FilterConditionTemplateResponse>builder()
                        .content(tuple.getT1())
                        .total(tuple.getT2())
                        .page(filterConditionPageQueryRequest.getPage())
                        .pageSize(filterConditionPageQueryRequest.getPageSize())
                        .build());
    }

    @GetMapping("/{templateId}")
    Mono<FilterConditionTemplateResponse> getById(@PathVariable String templateId) {
        return filterConditions.getById(templateId)
                .map(FilterConditionTemplateResponse::from);
    }

    @PutMapping("/{templateId}")
    Mono<Void> update(@PathVariable String templateId, @RequestBody FilterConditionTemplateRequest filterConditionTemplateRequest) {
        return filterConditions.update(templateId, filterConditionTemplateRequest.to());
    }

    @DeleteMapping("/{templateId}")
    Mono<Void> delete(@PathVariable String templateId) {
        return filterConditions.delete(templateId);
    }
}
