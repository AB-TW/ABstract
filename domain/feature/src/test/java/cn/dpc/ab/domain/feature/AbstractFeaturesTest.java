package cn.dpc.ab.domain.feature;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AbstractFeaturesTest {

    @Test
    void should_update_success() {
        Features features = new MockSuccessFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");
        Feature.FeatureDescription description = Feature.FeatureDescription.builder()
                .name("name")
                .description("description")
                .build();

        features.update(id, description)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_update_return_error_when_id_not_exist() {
        Features features = new MockNotFoundFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");
        Feature.FeatureDescription description = Feature.FeatureDescription.builder()
                .name("name")
                .description("description")
                .build();

        features.update(id, description)
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    void should_enable_success() {
        Features features = new MockSuccessFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");

        features.enable(id)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_enable_return_error_when_id_not_exist() {
        Features features = new MockNotFoundFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");

        features.enable(id)
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    void should_disable_success() {
        Features features = new MockSuccessFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");

        features.disable(id)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_disable_return_error_when_id_not_exist() {
        Features features = new MockNotFoundFeatures();
        Feature.FeatureId id = new Feature.FeatureId("featureKey");

        features.disable(id)
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }


    static class MockSuccessFeatures extends AbstractFeatures {
        @Override
        public Mono<Void> doEnable(Feature feature) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> doUpdate(Feature feature, Feature.FeatureDescription description) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> doDisable(Feature feature) {
            return Mono.empty();
        }

        @Override
        public Mono<Feature> get(Feature.FeatureId id) {
            return Mono.just(new Feature(id,
                    Feature.FeatureDescription.builder().name("name").description("description").build()));
        }

        @Override
        public Flux<Feature> getAll(FilterParams params) {
            return null;
        }

        @Override
        public Flux<Feature> subCollection(FilterParams params, int from, int to) {
            return null;
        }

        @Override
        public Mono<Long> size(FilterParams params) {
            return null;
        }

        @Override
        public Flux<Feature> enabled(FilterParams params) {
            return null;
        }

        @Override
        public Mono<Feature> add(Feature feature) {
            return null;
        }
    }

    static class MockNotFoundFeatures extends AbstractFeatures {
        @Override
        public Mono<Void> doEnable(Feature feature) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> doUpdate(Feature feature, Feature.FeatureDescription description) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> doDisable(Feature feature) {
            return Mono.empty();
        }

        @Override
        public Mono<Feature> get(Feature.FeatureId id) {
            return Mono.empty();
        }

        @Override
        public Flux<Feature> getAll(FilterParams params) {
            return null;
        }

        @Override
        public Flux<Feature> subCollection(FilterParams params, int from, int to) {
            return null;
        }

        @Override
        public Mono<Long> size(FilterParams params) {
            return null;
        }

        @Override
        public Flux<Feature> enabled(FilterParams params) {
            return null;
        }

        @Override
        public Mono<Feature> add(Feature feature) {
            return null;
        }
    }
}

