package az.clinify.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
