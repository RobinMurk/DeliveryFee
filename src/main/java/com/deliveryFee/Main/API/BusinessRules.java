package com.deliveryFee.Main.API;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusinessRules {
    private double TALLIN_CAR_RBF = 4;
    private double TALLIN_SCOOTER_RBF = 3.5;
    private double TALLIN_BIKE_RBF = 3;
    private double TARTU_CAR_RBF = 3.5;
    private double TARTU_SCOOTER_RBF = 3;
    private double TARTU_BIKE_RBF = 2.5;
    private double PARNU_CAR_RBF = 3;
    private double PARNU_SCOOTER_RBF = 2.5;
    private double PARNU_BIKE_RBF = 2;
    private double EXTRA_FOR_RAIN = 0.5;
    private double EXTRA_FOR_SNOW = 1;
    private double EXTRA_FOR_WIND = 0.5;
    private double EXTRA_FOR_LOWER_TEMP = 1;
    private double EXTRA_FOR_LOW_TEMP = 0.5;

   //getters and setters
    public double getTALLIN_CAR_RBF() {
        return TALLIN_CAR_RBF;
    }

    public void setTALLIN_CAR_RBF(double TALLIN_CAR_RBF) {
        this.TALLIN_CAR_RBF = TALLIN_CAR_RBF;
    }

    public double getTALLIN_SCOOTER_RBF() {
        return TALLIN_SCOOTER_RBF;
    }

    public void setTALLIN_SCOOTER_RBF(double TALLIN_SCOOTER_RBF) {
        this.TALLIN_SCOOTER_RBF = TALLIN_SCOOTER_RBF;
    }

    public double getTALLIN_BIKE_RBF() {
        return TALLIN_BIKE_RBF;
    }

    public void setTALLIN_BIKE_RBF(double TALLIN_BIKE_RBF) {
        this.TALLIN_BIKE_RBF = TALLIN_BIKE_RBF;
    }

    public double getTARTU_CAR_RBF() {
        return TARTU_CAR_RBF;
    }

    public void setTARTU_CAR_RBF(double TARTU_CAR_RBF) {
        this.TARTU_CAR_RBF = TARTU_CAR_RBF;
    }

    public double getTARTU_SCOOTER_RBF() {
        return TARTU_SCOOTER_RBF;
    }

    public void setTARTU_SCOOTER_RBF(double TARTU_SCOOTER_RBF) {
        this.TARTU_SCOOTER_RBF = TARTU_SCOOTER_RBF;
    }

    public double getTARTU_BIKE_RBF() {
        return TARTU_BIKE_RBF;
    }

    public void setTARTU_BIKE_RBF(double TARTU_BIKE_RBF) {
        this.TARTU_BIKE_RBF = TARTU_BIKE_RBF;
    }

    public double getPARNU_CAR_RBF() {
        return PARNU_CAR_RBF;
    }

    public void setPARNU_CAR_RBF(double PARNU_CAR_RBF) {
        this.PARNU_CAR_RBF = PARNU_CAR_RBF;
    }

    public double getPARNU_SCOOTER_RBF() {
        return PARNU_SCOOTER_RBF;
    }

    public void setPARNU_SCOOTER_RBF(double PARNU_SCOOTER_RBF) {
        this.PARNU_SCOOTER_RBF = PARNU_SCOOTER_RBF;
    }

    public double getPARNU_BIKE_RBF() {
        return PARNU_BIKE_RBF;
    }

    public void setPARNU_BIKE_RBF(double PARNU_BIKE_RBF) {
        this.PARNU_BIKE_RBF = PARNU_BIKE_RBF;
    }

    public double getEXTRA_FOR_RAIN() {
        return EXTRA_FOR_RAIN;
    }

    public void setEXTRA_FOR_RAIN(double EXTRA_FOR_RAIN) {
        this.EXTRA_FOR_RAIN = EXTRA_FOR_RAIN;
    }

    public double getEXTRA_FOR_SNOW() {
        return EXTRA_FOR_SNOW;
    }

    public void setEXTRA_FOR_SNOW(double EXTRA_FOR_SNOW) {
        this.EXTRA_FOR_SNOW = EXTRA_FOR_SNOW;
    }

    public double getEXTRA_FOR_WIND() {
        return EXTRA_FOR_WIND;
    }

    public void setEXTRA_FOR_WIND(double EXTRA_FOR_WIND) {
        this.EXTRA_FOR_WIND = EXTRA_FOR_WIND;
    }

    public double getEXTRA_FOR_LOWER_TEMP() {
        return EXTRA_FOR_LOWER_TEMP;
    }

    public void setEXTRA_FOR_LOWER_TEMP(double EXTRA_FOR_LOWER_TEMP) {
        this.EXTRA_FOR_LOWER_TEMP = EXTRA_FOR_LOWER_TEMP;
    }

    public double getEXTRA_FOR_LOW_TEMP() {
        return EXTRA_FOR_LOW_TEMP;
    }

    public void setEXTRA_FOR_LOW_TEMP(double EXTRA_FOR_LOW_TEMP) {
        this.EXTRA_FOR_LOW_TEMP = EXTRA_FOR_LOW_TEMP;
    }
}
