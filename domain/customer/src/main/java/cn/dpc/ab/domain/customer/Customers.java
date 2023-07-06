package cn.dpc.ab.domain.customer;

import cn.dpc.ab.domain.core.CustomerId;
import reactor.core.publisher.Mono;

/**
 * Customers represents the customers.
 */
public interface Customers {

    /**
     * getById returns the customer by id.
     */
    Mono<Customer> getById(CustomerId customerId);
}
