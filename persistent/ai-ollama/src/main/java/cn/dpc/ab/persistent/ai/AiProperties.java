package cn.dpc.ab.persistent.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI properties.
 */
@Data
@ConfigurationProperties("ai")
@Component
public class AiProperties implements cn.dpc.ab.domain.ai.AiProperties {
    private boolean enabled = true;
    private float intentSimilarityThreshold = 0.7F;
    private float qaSimilarityThreshold = 0.7F;
}
