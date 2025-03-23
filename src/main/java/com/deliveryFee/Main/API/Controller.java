package com.deliveryFee.Main.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    public record PostRequest(String city, String vehicle) {}

    public record UpdateRuleRequest(Map<String, Double> ruleset) {}

    public record UpdateRuleResponse(String status, List<String> values) {}

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
        }catch (Exception e){
            if(request.city == null ||request.vehicle == null){
                return createPostResponse(-1, "required parameters do not exist", HttpStatus.BAD_REQUEST);
            }
            return createPostResponse(-1,"Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-rule")
    public ResponseEntity<UpdateRuleResponse> updateRule(@RequestBody UpdateRuleRequest request) {
        try {
            List<String> sucessfulValues = feeService.updateRules(request);
            return new ResponseEntity<>(
                    new UpdateRuleResponse("SUCCESS",sucessfulValues), HttpStatus.OK
            );
        }catch (APIException e){
            return new ResponseEntity<>(
                    new UpdateRuleResponse("error: " + e.getMessage(), List.of()), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private ResponseEntity<PostResponse> createPostResponse(double value, String statement, HttpStatus status){
        return new ResponseEntity<>(
                new PostResponse(value,statement), status
        );
    }


}
