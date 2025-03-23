package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.WeatherData;
import com.deliveryFee.Main.database.WeatherDataRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;




@Service
public class FeeService {

    private final WeatherDataRepository weatherDataRepository;
    private BusinessRules businessRules;

    public FeeService(WeatherDataRepository weatherDataRepository, BusinessRules businessRules) {
        this.weatherDataRepository = weatherDataRepository;
        this.businessRules = businessRules;

    }

    /**
     * Calculates the fee based on the given city and vehicle type. Latest weather data is used.
     * @param cityRaw  String, must by alphabetic only, no whitespaces
     * @param vehicleRaw  String, must be alphabetic only, no whitespaces
     * @return  double, fee ammount for the given parameters based on latest weather data
     * @throws APIException
     */
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

            sum += calculateRBF(city, vehicle);
            sum += calculateATEF(vehicle, data.getAirTemperature());
            sum += calculateWSEF(vehicle, data.getWindSpeed());
            sum += calculateWPEF(vehicle, data.getPhenomenon());
            return sum;
        }catch (NoSuchElementException e){
            throw new APIException(-1, "[!] unable to retrieve data for " + cityRaw);
        }catch (IllegalArgumentException e){
            throw new APIException(-1, e.getMessage());
        }
    }

    //sanitize input
    private boolean isValid(String rawInput) {
        return rawInput.matches("^[a-zA-ZäöüõÄÖÜÕ]+$");
    }

    private double calculateWPEF(VEHICLES vehicle, String phenomenon) throws IllegalArgumentException {
        if (vehicle == VEHICLES.SCOOTER || vehicle == VEHICLES.BIKE){
            String phenomenonLower = phenomenon.toLowerCase();
            //naive approach
            if (phenomenonLower.contains("snow") || phenomenonLower.contains("sleet")){
                return businessRules.getEXTRA_FOR_SNOW();
            } else if (phenomenonLower.contains("rain")) {
                return businessRules.getEXTRA_FOR_RAIN();
            } else if (phenomenonLower.contains("hail") || phenomenonLower.contains("glaze") || phenomenonLower.contains("thunder")) {
                throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
            }
        }
        return 0;
    }

    private double calculateWSEF(VEHICLES vehicle, double windSpeed) throws IllegalArgumentException {

        if(vehicle == VEHICLES.BIKE){
            if (windSpeed > 20) throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
            if (windSpeed >= 10) return businessRules.getEXTRA_FOR_WIND();
        }
        return 0;
    }

    private double calculateATEF(VEHICLES vehicle, double temp) {
        if(vehicle == VEHICLES.SCOOTER | vehicle == VEHICLES.BIKE){
            if (temp < -10){
                return businessRules.getEXTRA_FOR_LOWER_TEMP();
            } else if (-10 <= temp && temp <= 0) {
                return businessRules.getEXTRA_FOR_LOW_TEMP();
            }
        }
        return 0;
    }

    private double calculateRBF(CITIES city, VEHICLES vehicle) {
        return switch (city) {
            case TALLINN -> switch (vehicle) {
                case CAR -> businessRules.getTALLIN_CAR_RBF();
                case SCOOTER -> businessRules.getTALLIN_SCOOTER_RBF();
                case BIKE -> businessRules.getTALLIN_BIKE_RBF();
            };
            case TARTU -> switch (vehicle) {
                case CAR -> businessRules.getTARTU_CAR_RBF();
                case SCOOTER -> businessRules.getTARTU_SCOOTER_RBF();
                case BIKE -> businessRules.getTARTU_BIKE_RBF();
            };
            case PARNU -> switch (vehicle) {
                case CAR -> businessRules.getPARNU_CAR_RBF();
                case SCOOTER -> businessRules.getPARNU_SCOOTER_RBF();
                case BIKE -> businessRules.getPARNU_BIKE_RBF();
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
            default -> throw new IllegalArgumentException("Unexpected value: " + vehicle.toLowerCase());
        };
    }

    private CITIES mapToCity(String city) throws IllegalArgumentException {
        return switch (city.toLowerCase()){
            case "tallinn" -> CITIES.TALLINN;
            case "tartu" -> CITIES.TARTU;
            case "pärnu" -> CITIES.PARNU;
            default -> throw new IllegalArgumentException("Unexpected value: " + city);
        };
    }
}

