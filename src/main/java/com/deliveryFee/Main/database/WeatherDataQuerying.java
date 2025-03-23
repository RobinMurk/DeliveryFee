package com.deliveryFee.Main.database;

import com.deliveryFee.Main.API.FeeService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WeatherDataQuerying {

    private static final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private static final Logger logger = LoggerFactory.getLogger(FeeService.class);

    private final RestTemplate restTemplate;
    private final WeatherDataRepository repository;

    //order in data does not change on update
    private final int idxTallin;
    private final int idxTartu;
    private final int idxParnu;

    public WeatherDataQuerying(WeatherDataRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.idxTallin = 1;
        this.idxTartu = 9;
        this.idxParnu = 28;
    }


    /**
     * Fetches and persists the data to the DB
     */
    @Scheduled(cron = "1 * * * * *")
    public void persistData(){
        try {
            List<WeatherData> data = fetchData();
            if (!data.isEmpty()) {
                repository.saveAllAndFlush(data);
                logger.info("Database updated successfully");
                return;
            }
            logger.error("Was unable to update Database");
        }catch (Exception e){
            logger.error("[!] Exception at data persistance");
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
                logger.debug("[-] no elements in observations");
                return List.of();
            }

            List<WeatherData> finalData = List.of(
                    observations.getData().get(idxTallin),
                    observations.getData().get(idxTartu),
                    observations.getData().get(idxParnu)
            );

            finalData.forEach(el -> el.setTimestamp(timestamp));

            return finalData;

        }catch (JAXBException e){
            logger.error("unable to unmarshall data");
        }catch (RestClientException e){
            logger.error("bad URL or could not convert response to String");
        }

        //in case of failure
        logger.error("[!] Failed to get Data");
        return List.of();
    }
}

