package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.customer.Customers;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * CustomersImpl implements Customers.
 */
@Component
@RequiredArgsConstructor
public class CustomersImpl implements Customers {
    private final FeatureConfigDbRepository featureConfigDbRepository;

    @Override
    public Mono<Customer> getById(CustomerId customerId) {
        Customer customer = new Customer(customerId);
        customer.setFeatureConfigs(new CustomerFeatureConfigs(featureConfigDbRepository,
                customerId));
        return Mono.just(customer);
    }
}
