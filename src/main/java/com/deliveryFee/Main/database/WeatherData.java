package com.deliveryFee.Main.database;

import jakarta.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement(name="station")
@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private String wmoCode;
    private double airTemperature;
    private double windSpeed;
    private String phenomenon;
    private LocalDateTime timestamp;

    // Getters and Setters


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @XmlElement(name="name")
    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    @XmlElement(name="wmocode")
    public String getWmoCode() {
        return wmoCode;
    }
    public void setWmoCode(String wmoCode) {
        this.wmoCode = wmoCode;
    }
    @XmlElement(name="airtemperature")
    public double getAirTemperature() {
        return airTemperature;
    }
    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }
    @XmlElement(name="windspeed")
    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    @XmlElement(name="phenomenon")
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
