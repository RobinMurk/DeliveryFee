package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.WeatherDataRepository;
import org.springframework.stereotype.Service;

@Service
public class FeeService {

    private final WeatherDataRepository weatherDataRepository;

    public FeeService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

}

