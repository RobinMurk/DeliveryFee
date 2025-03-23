package com.deliveryFee.Main.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/delivery-fee")
public class Controller {

    public record PostRequest(String city, String vehicle) {}

    public record PostResponse(Optional<Double> fee, String statement){}

    private final FeeService feeService;

    public Controller(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping("/hello")
    public String Test(){
        return "hello";
    }

    @PostMapping("/get-fee")
    public ResponseEntity<PostResponse> getFee(@RequestBody PostRequest request) {
            CITIES city = mapToCity(request.city);
            VEHICLES vehicle = mapToVehicle(request.vehicle);
            double fee = feeService.calculateFee(city,vehicle);
            if (fee == -1){
                return new ResponseEntity<>(
                        new PostResponse(null,"Bad request"), HttpStatus.BAD_REQUEST
                );
            }
            return new ResponseEntity<>(
                    new PostResponse(Optional.of(fee),"Good request"),HttpStatus.OK
            );

    }

    private VEHICLES mapToVehicle(String vehicle) {
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
