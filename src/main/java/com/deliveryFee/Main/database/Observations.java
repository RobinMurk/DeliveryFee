package com.deliveryFee.Main.database;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name="observations")
public class Observations{

    private List<WeatherData> data;
    private long timestamp;

    public Observations(){
        this.data = new ArrayList<>();
    }

    @XmlElement(name="station")
    public List<WeatherData> getData(){
        return data;
    }

    @XmlElement(name="timestamp")
    public long getTimestamp(){
        return timestamp;
    }
}

