package cn.dpc.ab.api.feature;

import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.feature.dto.FeaturePageRequest;
import cn.dpc.ab.api.feature.dto.FeatureRequest;
import cn.dpc.ab.api.feature.dto.FeatureResponse;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * FeatureApi represents the API for features.
 */
@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
public class FeatureApi {
    private final Features features;

    @GetMapping
    Mono<PageResponse<FeatureResponse>> getByPage(FeaturePageRequest request) {
        Features.FilterParams filterParams = request.filterParams();
        return features.subCollection(filterParams, request.getFrom(), request.getTo())
                .map(FeatureResponse::from)
                .collectList().zipWith(features.size(filterParams))
                .map(tuple -> PageResponse.<FeatureResponse>builder()
                        .content(tuple.getT1())
                        .total(tuple.getT2())
                        .page(request.getPage())
                        .pageSize(request.getPageSize())
                        .build());
    }

    @PostMapping
    Mono<FeatureResponse> add(@RequestBody FeatureRequest request) {
        return features.add(request.toFeature())
                .map(FeatureResponse::from);
    }

    @GetMapping("/{featureKey}")
    Mono<FeatureResponse> get(@PathVariable String featureKey) {
        return features.get(Feature.FeatureId.builder().featureKey(featureKey).build())
                .map(FeatureResponse::from)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(featureKey)));
    }

    @PutMapping("/{featureKey}")
    Mono<Void> update(@PathVariable String featureKey, @RequestBody FeatureRequest request) {
        return features.update(Feature.FeatureId.builder().featureKey(featureKey).build(), request.toFeature().getDescription());
    }

    @PutMapping("/{featureKey}/enable")
    Mono<Void> enable(@PathVariable String featureKey) {
        return features.enable(Feature.FeatureId.builder().featureKey(featureKey).build());
    }

    @PutMapping("/{featureKey}/disable")
    Mono<Void> disable(@PathVariable String featureKey) {
        return features.disable(Feature.FeatureId.builder().featureKey(featureKey).build());
    }
}
