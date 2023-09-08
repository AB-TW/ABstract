package cn.dpc.ab.api.log;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

/**
 * RequestLogWebFilter.
 */
@Component
@Slf4j
public class RequestLogWebFilter implements WebFilter {
    static final Random random = new Random(System.currentTimeMillis());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final StringBuilder requestBody = new StringBuilder();
        final StringBuilder responseBody = new StringBuilder();


        ServerHttpRequestLogDecorator resDecorator = new ServerHttpRequestLogDecorator(exchange.getRequest().mutate().build(), requestBody);
        ServerHttpResponseLogDecorator respDecorator = new ServerHttpResponseLogDecorator(exchange.getResponse(), responseBody);
        ServerWebExchange resExchange = exchange.mutate()
                .request(resDecorator)
                .response(respDecorator)
                .build();
        return chain.filter(resExchange)
                .transformDeferred(call -> doFilter(resExchange, call, responseBody, requestBody));
    }

    private Publisher<Void> doFilter(ServerWebExchange exchange, Mono<Void> call,
                                     final StringBuilder responseBody, final StringBuilder requestBody) {
        long start = currentTimeMillis();

        final String traceId = "" + start + random.nextInt(1000000);
        ServerHttpRequest request = exchange.getRequest();
        return call.doOnSuccess(result -> logRequestAndResponse(exchange, responseBody, requestBody, traceId, request, start))
                .doOnError(throwable -> logRequestAndResponse(exchange, responseBody, requestBody, traceId, request, start));
    }

    /**
     * log Request And Response.
     *
     * @param exchange     exchange
     * @param responseBody responseBody
     * @param requestBody  requestBody
     * @param traceId      traceId
     * @param request      request
     * @param start        start
     */
    private void logRequestAndResponse(ServerWebExchange exchange, StringBuilder responseBody,
                                       StringBuilder requestBody, String traceId, ServerHttpRequest request, long start) {
        logRequest(requestBody, traceId, request.getURI().toString(), request.getMethod().name(), request.getHeaders());
        logResponse(exchange, responseBody.toString(), start, traceId, request.getMethod().name(), request.getURI().toString());
    }

    private void logRequest(StringBuilder requestBody, String traceId, String uri, String method, HttpHeaders headers) {
        log.info("[API-REQUEST, " + traceId + "] "
                + method + " " + uri + " headers: " + headers + ("GET".equalsIgnoreCase(method) ? "" : " body:" + requestBody));
    }

    private void logResponse(ServerWebExchange exchange, String responseBody, long start, String traceId,
                             String method, String uri) {
        long elapsedTime = currentTimeMillis() - start;
        HttpStatusCode responseCode = exchange.getResponse().getStatusCode();
        boolean isSuccess = responseCode != null && (responseCode.is2xxSuccessful() || responseCode.is3xxRedirection());
        String responseType = isSuccess ? "API-RESPONSE" : "API-ERROR";
        log.info("[" + responseType + ", " + traceId + "] " + method + " "
                + uri + ", eclipsed "
                + elapsedTime + "ms, status: " + responseCode
                + " body: " + responseBody);
    }
}
