package com.deliveryFee.Main.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    public record PostRequest(String city, String vehicle) {}

    public record PostResponse(double fee, String statement){}

    public record ErrorResponse(String status, String statement){}

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
            }catch (Exception e){
                if(request.city == null ||request.vehicle == null){
                    return new ResponseEntity<>(
                            new PostResponse(-1,"required parameters do not exist"), HttpStatus.BAD_REQUEST
                    );
                }
                return new ResponseEntity<>(
                        new PostResponse(-1, "Internal Server error"),HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
    }
}
