package com.All4Animal.server.client;

import com.All4Animal.server.dto.response.api.AnimalApiResponse;
import com.All4Animal.server.dto.response.api.AnimalApiWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AnimalApiClient {

    private final String serviceKey = "b2f977803123e50791f13db59911c91293a7c87448c01c10743ff11b9452eed6";
    private final String apiUrl = "http://apis.data.go.kr/1543061/abandonmentPublicService_v2/abandonmentPublic_v2";
    private final String[] cityCode = {"6110000", "6410000", "6280000"};

//    public List<AnimalApiResponse> fetchAnimals() {
//        RestTemplate restTemplate = new RestTemplate();
//
//        String url = UriComponentsBuilder.fromUriString(apiUrl)
//                .queryParam("serviceKey", serviceKey)
//                .queryParam("numOfRows", "1000")
//                .queryParam("pageNo", "1")
//                .toUriString();
//
//        AnimalApiWrapper response = restTemplate.getForObject(url, AnimalApiWrapper.class);
//
//        return (response != null && response.getBody() != null)
//                ? response.getBody().getItems()
//                : Collections.emptyList();
//    }

    public List<AnimalApiResponse> fetchAnimals() {
        RestTemplate restTemplate = new RestTemplate();
        List<AnimalApiResponse> allAnimals = new ArrayList<>();

        for (String code : cityCode) {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("numOfRows", "1000")
                    .queryParam("upr_cd", code)
                    .queryParam("pageNo", "1")
                    .toUriString();

            AnimalApiWrapper response = restTemplate.getForObject(url, AnimalApiWrapper.class);

            if(response != null && response.getBody() != null && response.getBody().getItems() != null) {
                allAnimals.addAll(response.getBody().getItems());
            }
        }

        return allAnimals;
    }
}
