package cn.dpc.ab.api.log;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * ServerHttpResponseLogDecorator.
 */
public class ServerHttpResponseLogDecorator extends ServerHttpResponseDecorator {

    private final StringBuilder responseBody;

    public ServerHttpResponseLogDecorator(ServerHttpResponse delegate, StringBuilder responseBody) {
        super(delegate);
        this.responseBody = responseBody;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Mono) {
            final Mono<DataBuffer> monoBody = (Mono<DataBuffer>) body;
            return super.writeWith(monoBody.map(this::handlerResponseBodyDataBufferAndLog));
        } else if (body instanceof Flux) {
            final Flux<DataBuffer> fluxBody = (Flux<DataBuffer>) body;
            return super.writeWith(fluxBody.map(this::handlerResponseBodyDataBufferAndLog));
        }
        return super.writeWith(body);
    }

    private DataBuffer handlerResponseBodyDataBufferAndLog(DataBuffer dataBuffer) {
        responseBody.append(dataBuffer.toString(StandardCharsets.UTF_8));
        return dataBuffer;
    }
}
