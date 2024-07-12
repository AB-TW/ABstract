package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.api.feature.dto.FeatureRequest;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;


/**
 * Configuration for function calls.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
public class FunctionCallConfig {

    private final Features features;
    private final Experiments experiments;

    /**
     * Add a new feature.
     *
     * @return the function
     */
    @Bean
    @Description("""
            Add or create a new feature to the system, return the feature key if success, return error message when fail
            调用前请校验补充必要参数
            成功后可在feature管理模块查看结果
            """)
    public Function<FeatureRequest, Response> addFeature() {
        return request ->  {
            Mono<Response> mono = Mono.just(request)
                    .map(FeatureRequest::toFeature)
                    .flatMap(features::add)
                    .map(Feature::getId)
                    .map(Feature.FeatureId::featureKey)
                    .map(featureKey -> new Response(true, featureKey, "success"));
            return handleResponse(mono);
        };
    }

    /**
     * Count feature.
     *
     * @return the countFeature function
     */
    @Bean
    @Description("Count the number of features in the system with optional filter parameters")
    public Function<Features.FilterParams, Response> countFeature() {
        return request -> {
            Mono<Response> mono = features.size(request)
                    .map(size -> new Response(true, size, "success"));
            return handleResponse(mono);
        };
    }

    /**
     * GetFeatureRequest Param.
     */
    public record GetFeatureRequest(String featureKey) {
    }

    /**
     * Get the feature by feature key.
     *
     * @return the getFeatureByFeatureKey function
     */
    @Bean
    @Description("Get the feature by feature key")
    public Function<GetFeatureRequest, Response> getFeatureByFeatureKey() {
        return request -> {
            Mono<Response> mono = features.get(Feature.FeatureId.builder().featureKey(request.featureKey).build())
                    .map(feature -> new Response(true, feature, "success"));
            return handleResponse(mono);
        };
    }

    /**
     * Count the experiment by feature key.
     *
     * @return the countExperiment function
     */
    @Bean
    @Description("Count the number of experiments in the system with optional filter parameters")
    public Function<Experiments.FilterParams, Response> countExperiment() {
        return request -> {
            Mono<Response> mono = experiments.size(request)
                    .map(size -> new Response(true, size, "success"));
            return handleResponse(mono);
        };
    }

    /**
     * Get the experiment by feature key.
     *
     * @return the getExperimentByFeatureKey function
     */
    @Bean
    @Description("Get the feature related experiment by feature key")
    public Function<GetExperimentRequest, Response> getExperimentByFeatureKey() {
        return request -> {
            Mono<Response> mono = experiments.getByFeatureKey(request.featureKey)
                    .map(experiment -> new Response(true, experiment, "success"));
            return handleResponse(mono);
        };
    }

    /**
     * GetFeatureRequest Param.
     */
    public record GetExperimentRequest(String featureKey) {
    }


    private static Response handleResponse(Mono<Response> mono) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        mono
                .onErrorResume(e -> Mono.just(new Response(false, null, e.getMessage())))
                .doOnSuccess(future::complete)
                .subscribe();

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return new Response(false, null, e.getMessage());
        }
    }

    /**
     * Response represents a response from the API.
     */
    public record Response(boolean success, Object result, String message) {
    }

    @Bean
    Set<String> functions() {
        return Set.of("countFeature", "getFeatureByFeatureKey", "getExperimentByFeatureKey", "countExperiment");
    }

}


