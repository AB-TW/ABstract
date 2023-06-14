package cn.dpc.ab.api.feature;

import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.feature.dto.*;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
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

    @GetMapping("/{featureKey}/configs")
    Mono<PageResponse<FeatureConfigResponse>> getConfigs(@PathVariable String featureKey, FeatureConfigPageRequest request) {
        Feature.Configs.FilterParams filterParams = request.filterParams();
        return getFeature(featureKey)
                .map(Feature::getConfigs)
                .flatMap(featureConfigs ->
                        featureConfigs.subCollection(filterParams, request.getFrom(), request.getTo())
                                .map(FeatureConfigResponse::from)
                                .collectList().zipWith(featureConfigs.size(filterParams))
                                .map(tuple -> PageResponse.<FeatureConfigResponse>builder()
                                        .content(tuple.getT1())
                                        .total(tuple.getT2())
                                        .page(request.getPage())
                                        .pageSize(request.getPageSize())
                                        .build()));
    }

    @PostMapping("/{featureKey}/configs")
    Mono<FeatureConfigResponse> addConfig(@PathVariable String featureKey, @RequestBody FeatureConfigRequest request) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().add(request.toFeatureConfig()))
                .map(FeatureConfigResponse::from);
    }

    @GetMapping("/{featureKey}/configs/{configId}")
    Mono<FeatureConfigResponse> getConfig(@PathVariable String featureKey, @PathVariable String configId) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().get(FeatureConfig.FeatureConfigId.builder()
                        .configId(configId).build()))
                .map(FeatureConfigResponse::from)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(configId)));
    }

    @PutMapping("/{featureKey}/configs/{configId}")
    Mono<Void> updateConfig(@PathVariable String featureKey, @PathVariable String configId,
                            @RequestBody FeatureConfigRequest request) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().update(FeatureConfig.FeatureConfigId.builder()
                        .configId(configId).build(), request.toFeatureConfig().getDescription()));
    }

    @PutMapping("/{featureKey}/configs/{configId}/publish")
    Mono<Void> publishConfig(@PathVariable String featureKey, @PathVariable String configId) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().publish(FeatureConfig.FeatureConfigId.builder()
                        .configId(configId).build()));
    }

    @PutMapping("/{featureKey}/configs/{configId}/disable")
    Mono<Void> disableConfig(@PathVariable String featureKey, @PathVariable String configId) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().disable(FeatureConfig.FeatureConfigId.builder()
                        .configId(configId).build()));
    }

    @DeleteMapping("/{featureKey}/configs/{configId}")
    Mono<Void> deleteConfig(@PathVariable String featureKey, @PathVariable String configId) {
        return getFeature(featureKey)
                .flatMap(feature -> feature.getConfigs().delete(FeatureConfig.FeatureConfigId.builder()
                        .configId(configId).build()));
    }

    private Mono<Feature> getFeature(String featureKey) {
        return features.get(Feature.FeatureId.builder().featureKey(featureKey).build())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Feature not found: " + featureKey)));
    }
}
