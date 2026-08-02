package pl.radar.flightradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.radar.flightradar.auth.OpenSkyAuthService;
import pl.radar.flightradar.domain.FlightSnapshot;
import pl.radar.flightradar.dto.OpenSkyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightIngestionService {

    private final WebClient webClient;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OpenSkyAuthService authService;

    @Scheduled(fixedRate = 3000)
    public void getData() {

        Mono<OpenSkyResponse> skyResponseMono = authService.getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri(uriBuilder -> {
                            return uriBuilder.path("states/all")
                                    .queryParam("lamin", 49.0)
                                    .queryParam("lomin", 14.0)
                                    .queryParam("lamax", 54.8)
                                    .queryParam("lomax", 24.1)
                                    .build();
                        })
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(OpenSkyResponse.class));

        List<FlightSnapshot> snapshotList = skyResponseMono.map(response ->
                        response.states().stream()
                                .filter(array -> array.length >= 7 && array[5] != null && array[6] != null)
                                .map(array -> {
                                    return new FlightSnapshot(
                                            (String) array[0], //icao24
                                            (String) array[1], //callsign
                                            (String) array[2], //origin country
                                            ((Number) array[5]).doubleValue(), //longitude
                                            ((Number) array[6]).doubleValue(), //latitude
                                            array[7] != null ? ((Number) array[7]).doubleValue() : 0.0, //altitudeBaro
                                            array[9] != null ? ((Number) array[9]).doubleValue() : 0.0, //velocity
                                            array[10] != null ? ((Number) array[10]).doubleValue() : 0.0); //true track (rotation on the map)
                                }).toList()
                )
                .onErrorResume(WebClientResponseException.TooManyRequests.class, e -> {
                    log.warn("Limit zapytań wyczerpany (429). Ignoruję i czekam na kolejny cykl.");
                    return Mono.just(java.util.Collections.emptyList());
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Wystąpił nieoczekiwany błąd podczas pobierania danych: {}", e.getMessage());
                    return Mono.just(java.util.Collections.emptyList());
                })
                .block();

        log.info("Pobrano listę aktywnych lotów w Polsce: {}", snapshotList.size());

        simpMessagingTemplate.convertAndSend("/topic/flights", snapshotList);

        snapshotList
                .stream().limit(5)
                .forEach(flight -> log.info("Samolot: ICAO: {}, Callsign: {}, Wysokość: {}", flight.icao24(), flight.callsign(), flight.altitudeBaro()));

    }


}
