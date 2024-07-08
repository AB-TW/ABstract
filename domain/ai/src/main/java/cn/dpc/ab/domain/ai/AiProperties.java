package cn.dpc.ab.domain.ai;

/**
 * AI properties.
 */
public interface AiProperties {
    boolean isEnabled();

    float getIntentSimilarityThreshold();

    float getQaSimilarityThreshold();
}
