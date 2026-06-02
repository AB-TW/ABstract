package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static cn.dpc.ab.domain.experiment.Assignment.EMPTY;
import static cn.dpc.ab.domain.experiment.Experiment.Status.DynamicStatus.*;

/**
 * CustomerAssignments provides for customer get assignments.
 */
@RequiredArgsConstructor
public class CustomerAssignments implements Customer.Assignments {
    private final Experiments experiments;

    private final CustomerId customerId;

    private final LocalCacheManager localCacheManager;

    @Override
    public Mono<Assignment> getByFeatureKey(Feature.FeatureId featureKey) {
        return getExperimentByFeatureKeyWithCache(featureKey)
                .flatMap(experiment -> {
                    // 过滤 NOT_BEGIN  DRAFT, STOPPED
                    if (Arrays.asList(NOT_BEGIN, DRAFT, STOPPED).contains(experiment.getDescription().status().getStatus())
                            || !StringUtils.hasLength(customerId.clientId())) {
                        return Mono.just(EMPTY);
                    }

                    return experiment.getAssignments().get(new Assignment.AssignmentId(experiment.getId(),
                                    customerId.clientId()))
                            .flatMap(assignment -> {
                                if (shouldClearBucketForCurrentFilter(experiment, assignment)) {
                                    return updateWithDefaultAssignment(experiment);
                                }
                                // 如果实验版本号有变化 则重新分组
                                if (shouldReassign(experiment, assignment)) {
                                    // experiment version updated  do reassign
                                    return Mono.empty();
                                }
                                //   assignment result with no customerId or customerId not match
                                if (shouldUpdateCustomerId(customerId, assignment)) {
                                    assignment.addCustomerId(customerId.customerId());
                                    return experiment.getAssignments().update(assignment);
                                }

                                return Mono.just(assignment);
                            }).switchIfEmpty(createAndSaveAssignment(experiment));
                })
                .defaultIfEmpty(EMPTY);
    }

    private Mono<Experiment> getExperimentByFeatureKeyWithCache(Feature.FeatureId featureKey) {
        String key = LocalCacheManager.EXPERIMENT_KEY_PREFIX + featureKey.featureKey();
        return localCacheManager.cacheMono(key, () -> experiments.getByFeatureKey(featureKey.featureKey()));
    }

    private boolean shouldReassign(Experiment experiment, Assignment assignment) {
        return !Objects.equals(experiment.getDescription().configVersion(), assignment.getDescription().experimentConfigVersion())
                && (Optional.ofNullable(assignment.getDescription().bucket()).map(bucket -> bucket.key() == null)
                .orElse(true));
    }

    private boolean shouldClearBucketForCurrentFilter(Experiment experiment, Assignment assignment) {
        return Optional.ofNullable(assignment.getDescription())
                .map(Assignment.Description::bucket)
                .isPresent()
                && !matchesCurrentFilter(experiment);
    }

    private boolean matchesCurrentFilter(Experiment experiment) {
        return Optional.ofNullable(experiment.getDescription())
                .map(Experiment.Description::filterCondition)
                .map(filterCondition -> filterCondition.match(customerId))
                .orElse(true);
    }

    private Mono<Assignment> createAndSaveAssignment(Experiment experiment) {
        return Assignment.createAssignment(experiment, customerId)
                .flatMap(assignment -> addOrUpdateAssignment(experiment, assignment));
    }

    private Mono<Assignment> updateWithDefaultAssignment(Experiment experiment) {
        return addOrUpdateAssignment(experiment, Assignment.createDefaultAssignment(experiment, customerId));
    }

    private Mono<Assignment> addOrUpdateAssignment(Experiment experiment, Assignment assignment) {
        return EMPTY == assignment
                ? Mono.just(assignment)
                : experiment.getAssignments().addOrUpdate(assignment).cast(Assignment.class);
    }

    private boolean shouldUpdateCustomerId(CustomerId customerId, Assignment assignment) {
        return StringUtils.hasLength(customerId.customerId())
                && !assignment.containsCustomerId(customerId);
    }
}
