package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.TestConfiguration;
import cn.dpc.ab.api.customer.dto.FeatureConfigResponse;
import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.customer.Customers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static cn.dpc.ab.api.customer.CustomerTestUtil.createFeatureConfig;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = CustomerApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class CustomerApiTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    Customers customers;

    @Mock
    Customer.FeatureConfigs configs;

    @Mock
    Customer.Assignments assignments;

    @Test
    void should_getFeatureConfigs_return_empty_when_customers_return_empty() {
        when(customers.getById(Mockito.any()))
                .thenReturn(Mono.empty());

        webClient.post()
                .uri("/customers/featureConfigs/{featureKey}", "key")
                .body(Mono.just(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build()), CustomerId.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FeatureConfigResponse.class)
                .hasSize(0);
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


        webClient.post()
                .uri("/customers/featureConfigs/{featureKey}", "key")
                .body(Mono.just(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build()), CustomerId.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FeatureConfigResponse.class)
                .hasSize(0);
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


        webClient.post()
                .uri("/customers/featureConfigs/{featureKey}", featureKey)
                .body(Mono.just(CustomerId.builder()
                        .customerId("customerId")
                        .clientId("clientId")
                        .build()), CustomerId.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FeatureConfigResponse.class)
                .hasSize(2);
    }
}
