package pl.radar.flightradar.dto;


import java.util.List;

public record OpenSkyResponse(Long Time, List<Object[]> states) {
}
