package pl.radar.flightradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.radar.flightradar.domain.FlightSnapshot;
import pl.radar.flightradar.dto.OpenSkyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class FlightIngestionService {

    private final WebClient webClient;

    public FlightIngestionService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Scheduled(fixedRate = 4000)
    public void getData() {

        Mono<OpenSkyResponse> skyResponseMono = webClient.get()
                .uri("states/all?lamin=49.5&lomin=15.0&lamax=54.0&lomax=23.5")
                .retrieve()
                .bodyToMono(OpenSkyResponse.class);

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
        ).block();

        log.info("Pobrano listę aktywnych lotów w Polsce: {}", snapshotList.size());

        snapshotList
                .stream().limit(5)
                .forEach(flight -> log.info("Samolot: ICAO: {}, Callsign: {}, Wysokość: {}", flight.icao24(), flight.callsign(), flight.altitudeBaro()));

    }


}
