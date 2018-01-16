package server.config;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import server.utils.HasLogger;

import java.io.IOException;

public class RestEventHandler implements HasLogger, ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        getLogger().error("Error occured while handlind http response: {}", response.getRawStatusCode());
    }
}
