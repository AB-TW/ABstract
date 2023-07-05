package cn.dpc.ab.domain.customer;

import reactor.core.publisher.Mono;

/**
 * Customers represents the customers.
 */
public interface Customers {

    /**
     * getById returns the customer by id.
     */
    Mono<Customer> getById(Customer.CustomerId customerId);
}
