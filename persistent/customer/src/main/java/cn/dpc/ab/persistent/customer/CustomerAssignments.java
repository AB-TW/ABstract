package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

import static cn.dpc.ab.domain.experiment.Assignment.EMPTY;
import static cn.dpc.ab.domain.experiment.Experiment.Status.DynamicStatus.*;

/**
 * CustomerAssignments provides for customer get assignments.
 */
@RequiredArgsConstructor
public class CustomerAssignments implements Customer.Assignments {
    private final Experiments experiments;
    private final Assignments assignments;

    private final CustomerId customerId;

    @Override
    public Mono<Assignment> getByFeatureKey(Feature.FeatureId featureKey) {
        return experiments.getByFeatureKey(featureKey.featureKey())
                .next()
                .flatMap(experiment -> {
                    // 过滤 NOT_BEGIN  DRAFT, STOPPED
                    if (Arrays.asList(NOT_BEGIN, DRAFT, STOPPED).contains(experiment.getDescription().status().getStatus())
                            || !StringUtils.hasLength(customerId.clientId())) {
                        return Mono.just(EMPTY);
                    }

                    return assignments.get(new Assignment.AssignmentId(experiment.getId(), customerId.clientId()))
                            .flatMap(assignment -> {
                                // 如果实验版本号有变化 则重新分组
                                if (shouldReassign(experiment, assignment)) {
                                    // experiment version updated  do reassign
                                    return Mono.empty();
                                }
                                //   assignment result with no customerId or customerId not match
                                if (shouldUpdateCustomerId(customerId, assignment)) {
                                    assignment.addCustomerId(customerId.customerId());
                                    return assignments.add(assignment);
                                }

                                return Mono.just(assignment);
                            }).switchIfEmpty(Assignment.createAssignment(experiment, customerId)
                                    .flatMap(assignment ->
                                            EMPTY == assignment ? Mono.just(assignment)
                                                    : assignments.add(assignment)));
                })
                .defaultIfEmpty(EMPTY);
    }

    private boolean shouldReassign(Experiment experiment, Assignment assignment) {
        return !Objects.equals(experiment.getDescription().configVersion(), assignment.getDescription().experimentConfigVersion())
                && (assignment.getDescription().bucket().key() == null);
    }

    private boolean shouldUpdateCustomerId(CustomerId customerId, Assignment assignment) {
        return StringUtils.hasLength(customerId.customerId())
                && !assignment.containsCustomerId(customerId);
    }
}
