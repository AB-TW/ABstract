package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.api.feature.dto.FeatureRequest;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import reactor.core.scheduler.Schedulers;


/**
 * Configuration for function calls.
 */
@Configuration
@RequiredArgsConstructor
public class FunctionCallConfig {

    private final Features features;

    /**
     * Add a new feature.
     *
     * @return the function callback
     */
    @Bean
    public FunctionCallback addFeature() {
        return FunctionCallbackWrapper.builder(new AddFeatureFunction())
                .withName("AddNewFeature")
                .withDescription("""
                    Add or create a new feature to the system, return the feature key if success, return error message when fail
                    调用前请校验补充必要参数
                    成功后可在feature管理模块查看结果
                    """)
                .build();
    }

    /**
     * Function to add a new feature.
     */
    public class AddFeatureFunction implements Function<FeatureRequest, Disposable>  {
        @Override
        public Disposable apply(FeatureRequest request) {
            return Mono.just(request)
                    .map(FeatureRequest::toFeature)
                    .flatMap(features::add)
                    .map(Feature::getId)
                    .map(Feature.FeatureId::featureKey)
                    .map(featureKey -> new Response(true, featureKey, "success"))
                    .onErrorResume(e -> Mono.just(new Response(false, null, e.getMessage())))
                    .publishOn(Schedulers.boundedElastic())
                    .subscribe();
        }
    }

    /**
     * Response represents a response from the API.
     */
    public record Response(boolean success, Object result, String message) {
    }
}


