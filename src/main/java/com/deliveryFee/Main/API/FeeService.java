package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.WeatherData;
import com.deliveryFee.Main.database.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class FeeService {

    private final WeatherDataRepository weatherDataRepository;
    private BusinessRules businessRules;
    private static final Logger logger = LoggerFactory.getLogger(FeeService.class);

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

    public List<String> updateRules(Controller.UpdateRuleRequest request) throws APIException {
        List<Runnable> callbacks = new ArrayList<>();
        //return list of rules that are able to be changed
        List<String> keys = new ArrayList<>();

            request.ruleset().forEach((key, value) -> {
                switch (key) {
                    case "TALLIN_CAR_RBF":
                        callbacks.add(() -> businessRules.setTALLIN_CAR_RBF(value));
                        keys.add(key);
                        break;
                    case "TALLIN_SCOOTER_RBF":
                        callbacks.add(() -> businessRules.setTALLIN_SCOOTER_RBF(value));
                        keys.add(key);
                        break;
                    case "TALLIN_BIKE_RBF":
                        callbacks.add(() -> businessRules.setTALLIN_BIKE_RBF(value));
                        keys.add(key);
                        break;
                    case "TARTU_CAR_RBF":
                        callbacks.add(() -> businessRules.setTARTU_CAR_RBF(value));
                        keys.add(key);
                        break;
                    case "TARTU_SCOOTER_RBF":
                        callbacks.add(() -> businessRules.setTARTU_SCOOTER_RBF(value));
                        keys.add(key);
                        break;
                    case "TARTU_BIKE_RBF":
                        callbacks.add(() -> businessRules.setTARTU_BIKE_RBF(value));
                        keys.add(key);
                        break;
                    case "PARNU_CAR_RBF":
                        callbacks.add(() -> businessRules.setPARNU_CAR_RBF(value));
                        keys.add(key);
                        break;
                    case "PARNU_SCOOTER_RBF":
                        callbacks.add(() -> businessRules.setPARNU_SCOOTER_RBF(value));
                        keys.add(key);
                        break;
                    case "PARNU_BIKE_RBF":
                        callbacks.add(() -> businessRules.setPARNU_BIKE_RBF(value));
                        keys.add(key);
                        break;
                    case "EXTRA_FOR_RAIN":
                        callbacks.add(() -> businessRules.setEXTRA_FOR_RAIN(value));
                        keys.add(key);
                        break;
                    case "EXTRA_FOR_SNOW":
                        callbacks.add(() -> businessRules.setEXTRA_FOR_SNOW(value));
                        keys.add(key);
                        break;
                    case "EXTRA_FOR_WIND":
                        callbacks.add(() -> businessRules.setEXTRA_FOR_WIND(value));
                        keys.add(key);
                        break;
                    case "EXTRA_FOR_LOWER_TEMP":
                        callbacks.add(() -> businessRules.setEXTRA_FOR_LOWER_TEMP(value));
                        keys.add(key);
                        break;
                    case "EXTRA_FOR_LOW_TEMP":
                        callbacks.add(() -> businessRules.setEXTRA_FOR_LOW_TEMP(value));
                        keys.add(key);
                        break;
                    default:
                        break;
                }
            });
        try {
            callbacks.forEach(Runnable::run);
        }catch (Exception e){
            logger.error("[!] Some ruleSet changes where not applied");
            throw new APIException(-1, "Some methods where not updated");
        }
        return keys;
    }
}

