package cn.dpc.ab.persistent.event;

import cn.dpc.ab.domain.event.FeatureConfigUpdatedEvent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * FeatureConfigUpdatedEventImpl implements FeatureConfigUpdatedEvent.
 */
@Getter
public class FeatureConfigUpdatedEventImpl extends ApplicationEvent implements FeatureConfigUpdatedEvent {
    private final String featureKey;

    public FeatureConfigUpdatedEventImpl(String featureKey) {
        super(featureKey);
        this.featureKey = featureKey;
    }
}
