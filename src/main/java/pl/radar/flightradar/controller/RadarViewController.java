package pl.radar.flightradar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RadarViewController {

    @GetMapping("/radar")
    public String showRadar() {
        return "radar";
    }

}