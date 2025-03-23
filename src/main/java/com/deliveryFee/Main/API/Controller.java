package com.deliveryFee.Main.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    public record PostRequest(String city, String vehicle) {}

    public record PostResponse(double fee, String statement){}

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

            try {
                double fee = feeService.calculateFee(request.city, request.vehicle);
                return new ResponseEntity<>(
                        new PostResponse(fee, "SUCCESS"), HttpStatus.OK
                );
            }catch (APIException e){
                return new ResponseEntity<>(
                        new PostResponse(e.getValue(), e.getMessage()), HttpStatus.BAD_REQUEST
                );
            }
    }
}
