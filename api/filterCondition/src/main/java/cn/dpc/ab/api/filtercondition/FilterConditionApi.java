package cn.dpc.ab.api.filtercondition;

import cn.dpc.ab.api.filtercondition.dto.FilterConditionTemplateResponse;
import cn.dpc.ab.domain.core.FilterConditions;
import cn.dpc.ab.api.filtercondition.dto.FilterConditionTemplateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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
    Flux<FilterConditionTemplateResponse> getAll() {
        return filterConditions.getAll()
                .map(FilterConditionTemplateResponse::from);
    }
}
