package pl.radar.flightradar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RadarController {

    @GetMapping("/")
    public String showRadarMap() {
        return "index";
    }
}