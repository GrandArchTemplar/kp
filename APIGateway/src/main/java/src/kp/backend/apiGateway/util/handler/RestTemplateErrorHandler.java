package src.kp.backend.apiGateway.util.handler;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import src.kp.backend.apiGateway.exception.HTTPRequestException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getRawStatusCode() != 251
                && (clientHttpResponse.getStatusCode().is4xxClientError()
                || clientHttpResponse.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        throw new HTTPRequestException(StreamUtils.copyToString(
                clientHttpResponse.getBody(), StandardCharsets.UTF_8), clientHttpResponse.getStatusCode()
        );
    }
}
