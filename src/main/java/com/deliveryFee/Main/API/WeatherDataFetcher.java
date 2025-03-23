package com.deliveryFee.Main.API;

import com.deliveryFee.Main.database.Observations;
import com.deliveryFee.Main.database.WeatherData;
import com.deliveryFee.Main.database.WeatherDataRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Component
public class WeatherDataFetcher {

    private static final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private final RestTemplate restTemplate;
    private final WeatherDataRepository repository;

    public WeatherDataFetcher(WeatherDataRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }



    @Scheduled(cron = "* * * * * *")
    public void persistData(){
        try {
            List<WeatherData> data = fetchData();
            if (!data.isEmpty()) {
                repository.saveAllAndFlush(data);
                System.out.println("[+] " + data.getFirst().getTimestamp() + " Database updated successfully");
                return;
            }
            System.out.println("[!] " + LocalDateTime.now() + "Was unable to update Database");
        }catch (Exception e){
            System.out.println("[!] Exception at data persistance");
        }
    }

    private List<WeatherData> fetchData() {
        try {
            //query data from URL
            String response = restTemplate.getForObject(URL, String.class);

            //unmarshall the data to Observations object
            Unmarshaller unmarshaller = JAXBContext.newInstance(Observations.class).createUnmarshaller();
            Observations observations = (Observations) unmarshaller.unmarshal(new StringReader(response));

            //get date from inside the <observations> tag
            LocalDateTime timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(observations.getTimestamp()), ZoneId.of("Europe/Tallinn"));

            if(observations.getData().isEmpty()){
                System.out.println("[!] no elements in observations");
                return List.of();
            }
            for (WeatherData data : observations.getData()){
                data.setTimestamp(timestamp);
            }

            return observations.getData();

        }catch (JAXBException e){
            System.out.println("[!] " + LocalDateTime.now() + ": unable to unmarshall data" + e.getStackTrace());
        }catch (RestClientException e){
            System.out.println("[!] " + LocalDateTime.now() + ": Unable to GET URL: " + URL + ("or could not convert response to String"));
        }

        //in case of failure
        System.out.println("[!] Failed to get Data");
        return List.of();
    }
}

