package pl.radar.flightradar.interfaces;

import org.springframework.web.reactive.function.client.WebClient;

public interface IConnection {

    WebClient getConnection();

}
