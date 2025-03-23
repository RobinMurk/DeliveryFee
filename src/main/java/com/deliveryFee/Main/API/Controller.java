package com.deliveryFee.Main.API;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery-fee")
public class Controller {

    private final WeatherDataFetcher fetcher;

    public Controller(FeeService FeeService, WeatherDataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @GetMapping("/hello")
    public String Test(){
        return "hello";
    }

}
