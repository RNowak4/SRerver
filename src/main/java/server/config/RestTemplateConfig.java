package server.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    //    @Bean(name = "srRestTemplate")
    public RestTemplate srRestTemplate() {
        final RestTemplateBuilder builder = new RestTemplateBuilder();

        return builder.errorHandler(new RestEventHandler()).build();
    }
}
