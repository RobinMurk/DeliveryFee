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


    /**
     * Basic GET endpoint for testing server response
     * @return String "hello"
     */
    @GetMapping("/hello")
    public String Test(){
        return "hello";
    }

    /**
     * POST endpoint for getting the delivery fee based on most recent
     * Weather data
     * @param request PostRequest Record <br>
     *                request JSON format: <br>
     *                { <br>
     *                  "city" : CITY NAME HERE,<br>
     *                  "vehicle" : VEHICLE NAME HERE <br>
     *                } <br><br>
     * <bold>Example JSON:</bold><br>
     *                { <br>
     *                   "city" : "Tallinn",<br>
     *                   "vehicle" : "Car" <br>
     *                } <br>
     *
     *
     * @return PostResponse Record <br>
     * <bold>Example JSON response:</bold> <br>
     * { <br>
     *   "fee" : "Tallinn",<br>
     *   "statement" : "SUCCESS" <br>
     * } <br>
     * @throws APIException
     */
    @PostMapping("/get-fee")
    public ResponseEntity<PostResponse> getFee(@RequestBody PostRequest request) throws APIException {
            double fee = feeService.calculateFee(request.city, request.vehicle);
            return new ResponseEntity<>(
                    new PostResponse(fee, "SUCCESS"), HttpStatus.OK
            );
    }

    /**
     * Returns the Ruleset of all possible rules that can be manipulated through api/v1/update-rules endpoint
     * @return RuleList Record <br>
     * <br>
     * <bold>Example JSON response:</bold><br>
     * {
     *     "rules": [
     *         "TALLINN_CAR_RBF",
     *         "TALLINN_SCOOTER_RBF",
     *         "TALLINN_BIKE_RBF",
     *         "TARTU_CAR_RBF",
     *         "TARTU_SCOOTER_RBF",
     *         "TARTU_BIKE_RBF",
     *         "PARNU_CAR_RBF",
     *         "PARNU_SCOOTER_RBF",
     *         "PARNU_BIKE_RBF",
     *         "EXTRA_FOR_RAIN",
     *         "EXTRA_FOR_SNOW",
     *         "EXTRA_FOR_WIND",
     *         "EXTRA_FOR_LOWER_TEMP",
     *         "EXTRA_FOR_LOW_TEMP"
     *     ]
     * }
     */
    @GetMapping("/get-ruleset")
    public ResponseEntity<RuleList> getRuleset(){
        return new ResponseEntity<>(
                new RuleList(feeService.getRules()), HttpStatus.OK
        );
    }

    /**
     * Updates all business rules with new values,
     * @param request UpdateRuleResponse Record, Contains a list of Rule Records,<br>
     *                each record has rule name and new value. <br>
     *                Correct rule names can be acquired from GET /api/v1/get-ruleset endpoint<br>
     *                or the getRuleset() method<br>
     *                <bold>Example JSON request:</bold><br>
     *                {
     *              "ruleset": [ <br>
     *                  { "rule": "TALLINN_CAR_RBF", "value": 5 },<br>
     *                  { "rule": "EXTRA_FOR_SNOW", "value": 10 }<br>
     *                  ]
     *              }
     * @return UpdateRuleResponse Record, that contains the rules that were successfully parsed.<br>
     * NB: This implementation is faulty. THIS DOES NOT GUARANTEE that the returned rules were successfully changed<br>
     * due to bad implementation <br>
     * //TODO fix proper handling of exceptions <br>
     * <bold>Example JSON response:</bold><br>
     * {
     *     "status": "SUCCESS",
     *     "values": [
     *         "TALLINN_CAR_RBF",
     *         "EXTRA_FOR_SNOW"
     *     ]
     * }
     * @throws APIException APIException with value of -1 and caught errror message
     */
    @PostMapping("/update-rules")
    public ResponseEntity<UpdateRuleResponse> updateRule(@RequestBody UpdateRuleRequest request) throws APIException {
            List<String> sucessfulValues = feeService.updateRules(request.ruleset());
            return new ResponseEntity<>(
                    new UpdateRuleResponse("SUCCESS",sucessfulValues), HttpStatus.OK
            );
    }

    /**
     * Handles all Exception class exceptions
     * @param e Exception class object
     * @return ErrorResponse Record with 500 response code<br>
     * <bold>Example:</bold><br>
     * { <br>
     *  "status" : "Unknown error",<br>
     *  "vehicle" : "Cannot invoke \"String.matches(String)\" because \"rawInput\" is null" <br>
     *} <br>
     */
    @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception e){
            return new ResponseEntity<>(
                    new ErrorResponse("Unknown error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    /**
     * Handles all APIExceptions
     * @param e The caught APIException
     * @return Error response with code 400 repsonse code<br>
     * <bold>Example JSON response:</bold><br>
     *{<br>
     *     "status": "Unexpected value: Tallidadann",<br>
     *     "Statement": "User Error"<br>
     * }
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponse> handleAPIexception(APIException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), "User Error"), HttpStatus.BAD_REQUEST
        );
    }

    private ResponseEntity<PostResponse> createPostResponse(double value, String statement, HttpStatus status){
        return new ResponseEntity<>(
                new PostResponse(value,statement), status
        );
    }
}
