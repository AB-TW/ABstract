package cn.dpc.ab.domain.core;

import lombok.Builder;

import java.util.Map;

/**
 * CustomerId represents the id of a customer.
 */
@Builder
public record CustomerId(String customerId, String clientId, Map<String, String> attributes) {
}
