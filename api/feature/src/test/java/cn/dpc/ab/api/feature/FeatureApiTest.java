package cn.dpc.ab.api.feature;

import cn.dpc.ab.api.TestConfiguration;
import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.feature.dto.FeatureConfigRequest;
import cn.dpc.ab.api.feature.dto.FeatureConfigResponse;
import cn.dpc.ab.api.feature.dto.FeatureRequest;
import cn.dpc.ab.api.feature.dto.FeatureResponse;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.domain.feature.Features;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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

    @Mock
    Feature.Configs configs;

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
        when(features.add(any())).thenReturn(Mono.just(createFeature("featureKey1", configs)));

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

    @Test
    public void should_getAll_configs_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        when(configs.subCollection(any(), anyLong(), anyLong())).thenReturn(Flux.just(createFeatureConfig(null, featureKey),
                createFeatureConfig(null, featureKey)));
        when(configs.size(any())).thenReturn(Mono.justOrEmpty(3L));

        webClient.get()
                .uri((builder) -> builder.path("/features/" + featureKey + "/configs")
                        .queryParam("page", 0)
                        .queryParam("pageSize", 2).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(pageResponse -> {
                    assertEquals(pageResponse.getTotal(), 3L);
                    assertEquals(pageResponse.getContent().size(), 2);
                    assertEquals(pageResponse.getPage(), 0);
                    assertEquals(pageResponse.getPageSize(), 2);
                });
    }

    @Test
    public void should_getAll_configs_return_not_found_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());

        webClient.get()
                .uri((builder) -> builder.path("/features/" + featureKey + "/configs")
                        .queryParam("page", 0)
                        .queryParam("pageSize", 2).build())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_add_config_success() {
        FeatureConfigRequest featureConfigRequest = createFeatureConfigRequest("featureKey1");
        when(features.get(any())).thenReturn(Mono.just(createFeature("featureKey1", configs)));
        when(configs.add(any(FeatureConfig.class))).thenReturn(Mono.just(createFeatureConfig(null, "featureKey1")));

        webClient.post()
                .uri("/features/{featureKey}/configs", "featureKey1")
                .body(Mono.just(featureConfigRequest), FeatureConfigRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureConfigResponse.class)
                .value(response -> assertEquals(response.getFeatureKey(), featureConfigRequest.getFeatureKey()));
    }

    @Test
    public void should_add_config_failed_when_feature_not_exist() {
        FeatureConfigRequest featureConfigRequest = createFeatureConfigRequest("featureKey1");
        when(features.get(any())).thenReturn(Mono.empty());

        webClient.post()
                .uri("/features/{featureKey}/configs", "featureKey1")
                .body(Mono.just(featureConfigRequest), FeatureConfigRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_update_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.get(any())).thenReturn(Mono.just(createFeatureConfig(configId, featureKey)));
        when(configs.update(any(), any())).thenReturn(Mono.empty());

        FeatureConfigRequest featureConfigRequest = createFeatureConfigRequest(featureKey);
        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .body(Mono.just(featureConfigRequest), FeatureConfigRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void should_update_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());
        String configId = "id";

        FeatureConfigRequest featureConfigRequest = createFeatureConfigRequest(featureKey);
        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .body(Mono.just(featureConfigRequest), FeatureConfigRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_update_failed_when_config_update_fail() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.update(any(), any())).thenReturn(Mono.error(new IllegalArgumentException("")));

        FeatureConfigRequest featureConfigRequest = createFeatureConfigRequest(featureKey);
        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .body(Mono.just(featureConfigRequest), FeatureConfigRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_delete_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.delete(any())).thenReturn(Mono.empty());

        webClient.delete()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void should_delete_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());
        String configId = "id";

        webClient.delete()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_delete_failed_when_config_delete_fail() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.delete(any())).thenReturn(Mono.error(new IllegalArgumentException("")));

        webClient.delete()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_get_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.get(any())).thenReturn(Mono.just(createFeatureConfig(configId, featureKey)));

        webClient.get()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureConfigResponse.class)
                .value(response -> assertEquals(response.getFeatureKey(), featureKey));
    }

    @Test
    public void should_get_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());
        String configId = "id";

        webClient.get()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_get_failed_when_config_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));
        String configId = "id";
        when(configs.get(any())).thenReturn(Mono.empty());

        webClient.get()
                .uri("/features/{featureKey}/configs/{configId}", featureKey, configId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void should_publish_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.publish(any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/publish", featureKey, configId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void should_publish_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());

        String configId = "id";

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/publish", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_publish_failed_when_config_publish_fail() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.publish(any())).thenReturn(Mono.error(new IllegalArgumentException("")));

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/publish", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_disable_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.disable(any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/disable", featureKey, configId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void should_disable_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());

        String configId = "id";

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/disable", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_disable_failed_when_config_disable_fail() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.disable(any())).thenReturn(Mono.error(new IllegalArgumentException("")));

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/disable", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_top_success() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.top(any())).thenReturn(Mono.empty());

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/top", featureKey, configId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void should_top_failed_when_feature_not_exist() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.empty());

        String configId = "id";

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/top", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_top_failed_when_config_top_fail() {
        String featureKey = "featureKey1";
        when(features.get(any())).thenReturn(Mono.just(createFeature(featureKey, configs)));

        String configId = "id";
        when(configs.top(any())).thenReturn(Mono.error(new IllegalArgumentException("")));

        webClient.put()
                .uri("/features/{featureKey}/configs/{configId}/top", featureKey, configId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    public static Feature createFeature(String featureKey) {
        return createFeature(featureKey, null);
    }

    public static Feature createFeature(String featureKey, Feature.Configs configs) {
        Feature feature = new Feature(new Feature.FeatureId(featureKey),
                Feature.Description.builder()
                        .description("description")
                        .name("name")
                        .enabled(false)
                        .template(null)
                        .build());

        feature.setConfigs(configs);
        return feature;
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

    public static FeatureConfig createFeatureConfig(String id, String featureKey) {
        return FeatureConfig.builder()
                .id(FeatureConfig.FeatureConfigId.builder().configId(null == id ? UUID.randomUUID().toString() : id).build())
                .featureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                .description(FeatureConfig.Description.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .description("description")
                        .title("title")
                        .data(Map.of("key", "value"))
                        .saData(Map.of("key", "value"))
                        .priority(0)
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(FeatureConfig.Status.StaticStatus.DRAFT)
                                .build())
                        .build())
                .build();
    }

    public static FeatureConfigRequest createFeatureConfigRequest(String featureKey) {
        return FeatureConfigRequest.builder()
                .featureKey(featureKey)
                .title("title")
                .description("description")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .key("key")
                .data(Map.of("key", "value"))
                .saData(Map.of("key", "value"))
                .priority(0)
                .status(FeatureConfig.Status.StaticStatus.DRAFT)
                .build();
    }

}