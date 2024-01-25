package cn.dpc.ab.api.customer.event;

import cn.dpc.ab.api.customer.config.RsocketConfigProperties;
import cn.dpc.ab.api.customer.config.RsocketRequesterStore;
import cn.dpc.ab.domain.event.FeatureConfigUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * FeatureConfigUpdatedPushListener.
 */
@Component
@RequiredArgsConstructor
public class FeatureConfigUpdatedPushListener implements ApplicationListener<ApplicationEvent> {
    private final RsocketConfigProperties rsocketConfigProperties;

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (!rsocketConfigProperties.isFeatureKeyPushEnabled()) {
            return;
        }

        if (event instanceof FeatureConfigUpdatedEvent) {
            RsocketRequesterStore.requesters.forEach((k, v) -> {
                var featureKey = ((FeatureConfigUpdatedEvent) event).getFeatureKey();
                Optional.ofNullable(v.get(featureKey))
                        .ifPresent(requester -> {
                            if (requester.isDisposed()) {
                                v.remove(featureKey);
                                return;
                            }

                            requester.route("feature.changed")
                                    .data(featureKey)
                                    .send()
                                    .subscribe();
                        });
            });
        }
    }
}
