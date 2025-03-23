package com.deliveryFee.Main.database;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name="observations")
public class Observations{

    private List<WeatherData> data;

    @XmlElement(name="station")
    public List<WeatherData> getData(){
        return data;
    }
}

