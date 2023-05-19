package cn.dpc.ab.domain.feature;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature test.
 */
class FeatureTest {
    @Test
    public void should_enable_feature() {
        // given
        Feature feature = getFeature();

        // when
        feature.enable();

        // then
        assertTrue(feature.getDescription().enabled(), "should be enabled");
    }

    @Test
    public void should_disable_feature() {
        // given
        Feature feature = getFeature();

        // when
        feature.disable();

        // then
        assertFalse(feature.getDescription().enabled(), "should be disabled");
    }

    @Test
    public void should_update_feature() {
        // given
        Feature feature = getFeature();

        // when
        feature.update(Feature.Description.builder()
                .name("name1")
                .description("description1")
                .template(Feature.FeatureTemplate.builder()
                        .dataType(Feature.FeatureTemplate.DataType.BOOLEAN)
                        .key("key")
                        .name("name")
                        .description("description")
                        .validator(Feature.FeatureTemplate.Validator.builder()
                                .required(true)
                                .build())
                        .defaultValue("true")
                        .build())
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        // then
        assertEquals(feature.getDescription().name(), "name1", "should be name");
        assertEquals(feature.getDescription().description(), "description1", "should be description");
        assertNotNull(feature.getDescription().template(), "should not be null");
        assertFalse(feature.getDescription().enabled(), "should be disabled");
    }

    private Feature getFeature() {
        return Feature.builder()
                .id(new Feature.FeatureId("id"))
                .description(new Feature.Description("name", "description", null, false, null, null))
                .build();
    }
}