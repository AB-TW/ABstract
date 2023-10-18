package cn.dpc.ab.api.log;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

/**
 * ServerHttpRequestLogDecorator.
 */
public class ServerHttpRequestLogDecorator extends ServerHttpRequestDecorator {
    private final StringBuilder requestBody;

    public ServerHttpRequestLogDecorator(ServerHttpRequest delegate, StringBuilder requestBody) {
        super(delegate);
        this.requestBody = requestBody;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(dataBuffer -> requestBody.append(dataBuffer.toString(StandardCharsets.UTF_8)));
    }
}
