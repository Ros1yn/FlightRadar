package pl.radar.flightradar.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
@Slf4j
public class OpenSkyAuthService {

    @Value("${opensky.username}")
    private String username;

    @Value("${opensky.password}")
    private String password;

    private String currentToken;
    private Long tokenExpiryTime;

    private final WebClient authClient = WebClient.create("https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token");

    public Mono<String> getAccessToken() {

        if (currentToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return Mono.just(currentToken);
        }

        return authClient.post()
                .headers(header -> header.setBasicAuth(username, password))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    this.currentToken = jsonNode.get("access_token").asString();
                    int expiresIn = jsonNode.get("expires_in").asInt();

                    this.tokenExpiryTime = System.currentTimeMillis() + ((expiresIn - 30) * 1000L);
                    log.info("New token has been taken from OpenSky.");

                    return currentToken;
                });
    }
}
