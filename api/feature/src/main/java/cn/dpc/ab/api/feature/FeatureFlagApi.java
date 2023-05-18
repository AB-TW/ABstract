package cn.dpc.ab.api.feature;

import cn.dpc.ab.api.feature.dto.FeatureFlagRequest;
import cn.dpc.ab.api.feature.dto.FeatureFlagResponse;
import cn.dpc.ab.domain.feature.Features;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeatureFlagApi represents the API for feature flags.
 */
@RestController
@RequestMapping("/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagApi {
    private final Features features;


    @GetMapping
    Flux<FeatureFlagResponse> getAll(Features.FilterParams params) {
        return features.getAll(params)
                .map(FeatureFlagResponse::from);
    }

    @PostMapping
    Mono<FeatureFlagResponse> add(@RequestBody FeatureFlagRequest request) {
        return features.add(request.toFeatureFlag())
                .map(FeatureFlagResponse::from);
    }
}
