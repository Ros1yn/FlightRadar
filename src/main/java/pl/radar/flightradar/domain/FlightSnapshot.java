package pl.radar.flightradar.domain;

public record FlightSnapshot(
        String icao24,
        String callsign,
        String originCountry,
        Double longitude,
        Double latitude,
        Double altitudeBaro,
        Double velocity,
        Double trueTrack
){}

