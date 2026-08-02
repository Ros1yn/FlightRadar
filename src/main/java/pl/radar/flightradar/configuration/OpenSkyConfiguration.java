package pl.radar.flightradar.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import pl.radar.flightradar.auth.OpenSkyAuthService;
import pl.radar.flightradar.interfaces.IConnection;

@Configuration
@Slf4j

public class OpenSkyConfiguration implements IConnection {

    @Value("${opensky.username}")
    private String username;

    @Value("${opensky.password}")
    private String password;

    @Override
    @Bean
    public WebClient getConnection() {

        log.info("username {} - password {}", username, password);

        return WebClient.builder()
                .baseUrl("https://opensky-network.org/api/")
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(clientCodecConfigurer -> {
                                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(10000000);
                                })
                                .build())
                .build();
    }

}
