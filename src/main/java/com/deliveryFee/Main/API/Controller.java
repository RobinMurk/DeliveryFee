package com.deliveryFee.Main.API;

import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    public record PostRequest(String city, String vehicle) {}

    public record UpdateRuleRequest(List<Rule> ruleset) {}

    public record Rule(String rule, Double value){}

    public record UpdateRuleResponse(String status, List<String> values) {}

    public record PostResponse(double fee, String statement){}

    public record ErrorResponse(String status, String Statement) {}

    public record RuleList(List<String> rules) {}

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

    @GetMapping("/get-ruleset")
    public ResponseEntity<RuleList> getRuleset(){
        return new ResponseEntity<>(
                new RuleList(feeService.getRules()), HttpStatus.OK
        );
    }

    @PostMapping("/update-rules")
    public ResponseEntity<UpdateRuleResponse> updateRule(@RequestBody UpdateRuleRequest request) {
        try {
            List<String> sucessfulValues = feeService.updateRules(request.ruleset());
            return new ResponseEntity<>(
                    new UpdateRuleResponse("SUCCESS",sucessfulValues), HttpStatus.OK
            );
        }catch (APIException e){
            return new ResponseEntity<>(
                    new UpdateRuleResponse("error: " + e.getMessage(), List.of()), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception e){
            return new ResponseEntity<>(
                    new ErrorResponse("Unknown error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }


    private ResponseEntity<PostResponse> createPostResponse(double value, String statement, HttpStatus status){
        return new ResponseEntity<>(
                new PostResponse(value,statement), status
        );
    }
}
