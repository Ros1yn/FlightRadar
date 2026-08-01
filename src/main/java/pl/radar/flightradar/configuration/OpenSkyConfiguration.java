package pl.radar.flightradar.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import pl.radar.flightradar.interfaces.IConnection;

@Configuration
public class OpenSkyConfiguration implements IConnection {

    @Value("opensky.clientid")
    private String username;

    @Value("opensky.clientsecret")
    private String password;

    @Override
    @Bean
    public WebClient getConnection() {

        return WebClient.builder()
                .baseUrl("https://opensky-network.org/api")
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(clientCodecConfigurer -> {
                                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(10000000);
                                })
                                .build())
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();
    }

}
