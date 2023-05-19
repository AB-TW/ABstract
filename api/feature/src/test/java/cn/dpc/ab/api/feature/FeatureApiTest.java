package cn.dpc.ab.api.feature;

import cn.dpc.ab.api.TestConfiguration;
import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.feature.dto.FeatureRequest;
import cn.dpc.ab.api.feature.dto.FeatureResponse;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = FeatureApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class FeatureApiTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    Features features;

    @Test
    void should_getAll_success_when_no_records() {
        when(features.subCollection(any(Features.FilterParams.class), anyLong(), anyLong()))
                .thenReturn(Flux.empty());
        when(features.size(any())).thenReturn(Mono.justOrEmpty(0L));

        webClient.get()
                .uri((builder) -> builder.path("/features")
                        .queryParam("page", 0)
                        .queryParam("pageSize", 10).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(pageResponse -> {
                    assertEquals(pageResponse.getTotal(), 0);
                    assertEquals(pageResponse.getContent().size(), 0);
                    assertEquals(pageResponse.getPage(), 0);
                    assertEquals(pageResponse.getPageSize(), 10);
                });
    }

    @Test
    void should_getAll_success_when_has_records() {
        when(features.subCollection(any(Features.FilterParams.class), anyLong(), anyLong()))
                .thenReturn(Flux.just(createFeature("featureKey1"),
                createFeature("featureKey2"),
                createFeature("featureKey3")));

        when(features.size(any())).thenReturn(Mono.justOrEmpty(10L));

        webClient.get()
                .uri((builder) -> builder.path("/features")
                        .queryParam("page", 0)
                        .queryParam("pageSize", 3).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(pageResponse -> {
                    assertEquals(pageResponse.getTotal(), 10L);
                    assertEquals(pageResponse.getContent().size(), 3);
                    assertEquals(pageResponse.getPage(), 0);
                    assertEquals(pageResponse.getPageSize(), 3);
                });
    }

    @Test
    void should_getAll_page_return_0_when_total_record_not_meet_page() {
        when(features.subCollection(any(Features.FilterParams.class), anyLong(), anyLong()))
                .thenReturn(Flux.just(createFeature("featureKey1"),
                        createFeature("featureKey2"),
                        createFeature("featureKey3")));

        when(features.size(any())).thenReturn(Mono.justOrEmpty(10L));

        webClient.get()
                .uri((builder) -> builder.path("/features")
                        .queryParam("page", 1)
                        .queryParam("pageSize", 10).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(pageResponse -> {
                    assertEquals(pageResponse.getTotal(), 10L);
                    assertEquals(pageResponse.getContent().size(), 3);
                    assertEquals(pageResponse.getPage(), 0);
                    assertEquals(pageResponse.getPageSize(), 10);
                });
    }

    @Test
    void should_add_feature_success() {
        when(features.add(any())).thenReturn(Mono.just(createFeature("featureKey1")));

        webClient.post()
                .uri("/features")
                .body(Mono.just(createFeatureRequest("featureKey1")), FeatureRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureResponse.class)
                .value(response ->
                        assertEquals(response.getFeatureKey(), "featureKey1",
                                "feature key should be featureKey1"));
    }

    @Test
    void should_add_feature_failed_when_feature_key_is_duplicated() {
        when(features.add(any())).thenReturn(Mono.error(new DuplicateKeyException("feature key is duplicated")));

        webClient.post()
                .uri("/features")
                .body(Mono.just(createFeatureRequest("featureKey1")), FeatureRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_get_feature_success() {
        when(features.get(any())).thenReturn(Mono.just(createFeature("featureKey1")));

        webClient.get()
                .uri("/features/{featureKey}", "featureKey1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureResponse.class)
                .value(response ->
                        assertEquals(response.getFeatureKey(), "featureKey1",
                                "feature key should be featureKey1"));
    }

    @Test
    void should_get_feature_failed_when_feature_key_not_exist() {
        when(features.get(any())).thenReturn(Mono.empty());

        webClient.get()
                .uri("/features/{featureKey}", "featureKey1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void should_update_feature_success() {
        when(features.update(any(), any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}", "featureKey1")
                .body(Mono.just(createFeatureRequest("featureKey1")), FeatureRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_update_feature_failed_when_feature_key_not_exist() {
        when(features.update(any(), any())).thenReturn(Mono.error(new RecordNotFoundException("featureKey")));

        webClient.put()
                .uri("/features/{featureKey}", "featureKey1")
                .body(Mono.just(createFeatureRequest("featureKey1")), FeatureRequest.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    void should_enable_feature_success() {
        when(features.enable(any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}/enable", "featureKey1")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_enable_feature_failed_when_feature_key_not_exist() {
        when(features.enable(any())).thenReturn(Mono.error(new RecordNotFoundException("featureKey")));

        webClient.put()
                .uri("/features/{featureKey}/enable", "featureKey1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void should_disable_feature_success() {
        when(features.disable(any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}/disable", "featureKey1")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_disable_feature_failed_when_feature_key_not_exist() {
        when(features.disable(any())).thenReturn(Mono.error(new RecordNotFoundException("featureKey")));

        webClient.put()
                .uri("/features/{featureKey}/disable", "featureKey1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    public static Feature createFeature(String featureKey) {
        return new Feature(new Feature.FeatureId(featureKey),
                Feature.Description.builder()
                        .description("description")
                        .name("name")
                        .enabled(false)
                        .template(null)
                        .build());
    }

    public static FeatureRequest createFeatureRequest(String featureKey) {
        return FeatureRequest.builder()
                .name("name")
                .featureKey(featureKey)
                .description("description")
                .enabled(false)
                .template(null)
                .build();
    }

}