package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.WeatherData;
import com.deliveryFee.Main.database.WeatherDataRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FeeService {
    private static final double TALLIN_CAR_RBF = 4;
    private static final double TALLIN_SCOOTER_RBF = 3.5;
    private static final double TALLIN_BIKE_RBF = 3;
    private static final double TARTU_CAR_RBF = 3.5;
    private static final double TARTU_SCOOTER_RBF = 3;
    private static final double TARTU_BIKE_RBF = 2.5;
    private static final double PARNU_CAR_RBF = 3;
    private static final double PARNU_SCOOTER_RBF = 2.5;
    private static final double PARNU_BIKE_RBF = 2;

    private final WeatherDataRepository weatherDataRepository;

    public FeeService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;

    }

    public double calculateFee(String cityRaw, String vehicleRaw) throws APIException{

        double sum = 0;
        CITIES city;
        VEHICLES vehicle;

        try {
            //sanitize input
            if(!isValid(cityRaw) || !isValid(vehicleRaw)){
                throw new APIException(-1,"invalid user parameters, input must contain only alphabetic characters");
            }

            city = mapToCity(cityRaw);
            vehicle = mapToVehicle(vehicleRaw);
            WeatherData data = getRecentWeatherData(city);

            System.out.println(data.toString());

            sum += calculateRBF(city, vehicle);
            sum += calculateATEF(vehicle, data.getAirTemperature());
            sum += calculateWSEF(vehicle, data.getWindSpeed());
            sum += calculateWPEF(vehicle, data.getPhenomenon());
            return sum;
        }catch (NoSuchElementException e){
            throw new APIException(-1, "[!] unable to retrive data for " + cityRaw);
        }catch (IllegalArgumentException e){
            throw new APIException(-1, e.getMessage());
        }
    }

    //sanitize to prevent XSS
    private boolean isValid(String rawInput) {
        return rawInput.matches("^[a-zA-Z]+$");
    }

    private double calculateWPEF(VEHICLES vehicle, String phenomenon) throws IllegalArgumentException {
        if (vehicle == VEHICLES.SCOOTER || vehicle == VEHICLES.BIKE){
            String phenomenonLower = phenomenon.toLowerCase();
            //naive approach
            if (phenomenonLower.contains("snow") || phenomenonLower.contains("sleet")){
                return 1;
            } else if (phenomenonLower.contains("rain")) {
                return 0.5;
            } else if (phenomenonLower.contains("hail") || phenomenonLower.contains("glaze") || phenomenonLower.contains("thunder")) {
                throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
            }
        }
        return 0;
    }

    private double calculateWSEF(VEHICLES vehicle, double windSpeed) throws IllegalArgumentException {

        if(vehicle == VEHICLES.BIKE){
            if (windSpeed > 20) throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
            if (windSpeed >= 10) return 0.5;
        }
        return 0;
    }

    private double calculateATEF(VEHICLES vehicle, double temp) {
        if(vehicle == VEHICLES.SCOOTER | vehicle == VEHICLES.BIKE){
            if (temp < -10){
                return 1;
            } else if (-10 <= temp && temp <= 0) {
                return 0.5;
            }
        }
        return 0;
    }

    private double calculateRBF(CITIES city, VEHICLES vehicle) {
        return switch (city) {
            case TALLINN -> switch (vehicle) {
                case CAR -> 4;
                case SCOOTER -> 3.5;
                case BIKE -> 3;
            };
            case TARTU -> switch (vehicle) {
                case CAR -> 3.5;
                case SCOOTER -> 3;
                case BIKE -> 2.5;
            };
            case PARNU -> switch (vehicle) {
                case CAR -> 3;
                case SCOOTER -> 2.5;
                case BIKE -> 2;
            };
        };
    }

    private WeatherData getRecentWeatherData(CITIES city) throws NoSuchElementException {
        return switch (city) {
            case TALLINN -> weatherDataRepository.findByStationNameOrderByTimestampDesc("Tallinn-Harku").getFirst();
            case TARTU -> weatherDataRepository.findByStationNameOrderByTimestampDesc("Tartu-Tõravere").getFirst();
            case PARNU -> weatherDataRepository.findByStationNameOrderByTimestampDesc("Pärnu").getFirst();
        };
    }

    private VEHICLES mapToVehicle(String vehicle) throws IllegalArgumentException{
        return switch (vehicle.toLowerCase()){
            case "car" -> VEHICLES.CAR;
            case "scooter" -> VEHICLES.SCOOTER;
            case "bike" -> VEHICLES.BIKE;
            default -> throw new IllegalStateException("Unexpected value: " + vehicle.toLowerCase());
        };
    }

    private CITIES mapToCity(String city) throws IllegalArgumentException {
        return switch (city.toLowerCase()){
            case "tallinn" -> CITIES.TALLINN;
            case "tartu" -> CITIES.TARTU;
            case "pärnu" -> CITIES.PARNU;
            default -> throw new IllegalStateException("Unexpected value: " + city.toLowerCase());
        };
    }
}

