package com.deliveryFee.Main.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private String wmoCode;
    private double airTemperature;    //no decimal points for efficiency
    private double windSpeed;        //no decimal points for efficiency
    private String phenomenon;
    private LocalDateTime timestamp;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    public String getWmoCode() {
        return wmoCode;
    }
    public void setWmoCode(String wmoCode) {
        this.wmoCode = wmoCode;
    }
    public double getAirTemperature() {
        return airTemperature;
    }
    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }
    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    public String getPhenomenon() {
        return phenomenon;
    }
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

