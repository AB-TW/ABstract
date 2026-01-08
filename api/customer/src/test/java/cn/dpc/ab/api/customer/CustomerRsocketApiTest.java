package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.customer.dto.FeatureConfigResponse;
import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.customer.Customers;
import cn.dpc.ab.domain.feature.Feature;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static cn.dpc.ab.api.customer.CustomerTestUtil.createFeatureConfig;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CustomerRsocketApiTest extends RsocketBaseTest {
    @MockitoBean
    Customers customers;

    @Mock
    Customer.FeatureConfigs configs;

    @Mock
    Customer.Assignments assignments;

    @Test
    void should_getFeatureConfigs_return_empty_when_customers_return_empty() {
        when(customers.getById(Mockito.any()))
                .thenReturn(Mono.empty());

        requester.route("customers.featureConfigs.{key}", "key")
                .metadata(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build(), MediaType.APPLICATION_JSON)
                .retrieveFlux(FeatureConfigResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void should_getFeatureConfigs_return_empty_when_configs_return_empty() {
        when(customers.getById(Mockito.any()))
                .thenAnswer(invocation -> {
                    Customer customer = new Customer((CustomerId) invocation.getArguments()[0]);
                    customer.setFeatureConfigs(configs);
                    customer.setAssignments(assignments);
                    return Mono.just(customer);
                });

        when(configs.getByFeatureKey(Mockito.any()))
                .thenReturn(Flux.empty());
        when(assignments.getByFeatureKey(Mockito.any()))
                .thenReturn(Mono.empty());


        requester.route("customers.featureConfigs.{key}", "key")
                .metadata(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build(), MediaType.APPLICATION_JSON)
                .retrieveFlux(FeatureConfigResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void should_getFeatureConfigs_success() {
        when(customers.getById(Mockito.any()))
                .thenAnswer(invocation -> {
                    Customer customer = new Customer((CustomerId) invocation.getArguments()[0]);
                    customer.setFeatureConfigs(configs);
                    customer.setAssignments(assignments);
                    return Mono.just(customer);
                });

        String featureKey = "key";
        when(configs.getByFeatureKey(Mockito.any()))
                .thenReturn(Flux.just(createFeatureConfig("id1", featureKey),
                        createFeatureConfig("id2", featureKey)));

        when(assignments.getByFeatureKey(Mockito.any()))
                .thenReturn(Mono.empty());

        requester.route("customers.featureConfigs.{key}", featureKey)
                .metadata(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build(), MediaType.APPLICATION_JSON)
                .retrieveFlux(FeatureConfigResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void getMultiFeatureConfigs() {
        when(customers.getById(Mockito.any()))
                .thenAnswer(invocation -> {
                    Customer customer = new Customer((CustomerId) invocation.getArguments()[0]);
                    customer.setFeatureConfigs(configs);
                    customer.setAssignments(assignments);
                    return Mono.just(customer);
                });

        String featureKey1 = "featureKey1";
        String featureKey2 = "featureKey2";
        when(configs.getByFeatureKey(eq(Feature.FeatureId.builder().featureKey(featureKey1).build())))
                .thenReturn(Flux.just(createFeatureConfig("id1", featureKey1),
                        createFeatureConfig("id2", featureKey1)));
        when(configs.getByFeatureKey(eq(Feature.FeatureId.builder().featureKey(featureKey2).build())))
                .thenReturn(Flux.just(createFeatureConfig("id21", featureKey2)));

        when(assignments.getByFeatureKey(Mockito.any()))
                .thenReturn(Mono.empty());

        requester.route("customers.featureConfigs")
                .metadata(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build(), MediaType.APPLICATION_JSON)
                .data(Flux.just(featureKey1, featureKey2), String.class)
                .retrieveFlux(FeatureConfigResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }
}