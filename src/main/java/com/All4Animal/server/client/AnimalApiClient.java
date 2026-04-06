package com.All4Animal.server.client;

import com.All4Animal.server.dto.response.api.AnimalApiResponse;
import com.All4Animal.server.dto.response.api.AnimalApiWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Component
public class AnimalApiClient {

    private final String serviceKey = "YOUR_SERVICE_KEY";
    private final String apiUrl = "http://apis.data.go.kr/1543061/abandonmentPublicService_v2/abandonmentPublic_v2";

    public List<AnimalApiResponse> fetchAnimals() {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", "100")
                .queryParam("pageNo", "1")
                .toUriString();

        AnimalApiWrapper response = restTemplate.getForObject(url, AnimalApiWrapper.class);

        return (response != null && response.getBody() != null)
                ? response.getBody().getItems()
                : Collections.emptyList();
    }

}
