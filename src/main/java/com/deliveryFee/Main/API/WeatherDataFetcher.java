package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.WeatherDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class WeatherDataFetcher {

    private static final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private final RestTemplate restTemplate;

    public WeatherDataFetcher(WeatherDataRepository repository, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "15 * * * * *")
    public String fetchData(){
        String response = restTemplate.getForObject(URL,String.class);
        return response;
    }
}

