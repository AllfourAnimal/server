package com.All4Animal.server.client;

import com.All4Animal.server.dto.response.api.AnimalApiResponse;
import com.All4Animal.server.dto.response.api.AnimalApiWrapper;
import com.All4Animal.server.dto.response.api.SeoulAnimalApiResponse;
import com.All4Animal.server.dto.response.api.SeoulAnimalApiWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.All4Animal.server.dto.response.api.SeoulAnimalImageApiResponse;
import com.All4Animal.server.dto.response.api.SeoulAnimalImageApiWrapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimalApiClient {

    // 공공데이터포털
    private final String nationalServiceKey = "b2f977803123e50791f13db59911c91293a7c87448c01c10743ff11b9452eed6";
    private final String nationalApiUrl = "http://apis.data.go.kr/1543061/abandonmentPublicService_v2/abandonmentPublic_v2";
    private final String cityCode = "6110000"; // 서울

    // 서울열린데이터광장
    private final String seoulServiceKey = "656e6e6174776a643335634b635269";
    private final String seoulApiUrl = "http://openapi.seoul.go.kr:8088";

    public List<AnimalApiResponse> fetchNationalAnimals() {
        RestTemplate restTemplate = new RestTemplate();
        List<AnimalApiResponse> allAnimals = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(nationalApiUrl)
                .queryParam("serviceKey", nationalServiceKey)
                .queryParam("numOfRows", "1000")
                .queryParam("upr_cd", cityCode)
                .queryParam("pageNo", "1")
                .toUriString();

        AnimalApiWrapper response = restTemplate.getForObject(url, AnimalApiWrapper.class);

        if(response != null && response.getBody() != null && response.getBody().getItems() != null) {
            allAnimals.addAll(response.getBody().getItems());
        }

        return allAnimals;
    }

    public List<SeoulAnimalApiResponse> fetchSeoulAnimals() {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = String.format("%s/%s/json/vPetInfo/1/1000/", seoulApiUrl, seoulServiceKey);

        SeoulAnimalApiWrapper response = restTemplate.getForObject(requestUrl, SeoulAnimalApiWrapper.class);

        if (response != null
                && response.getVPetInfo() != null
                && response.getVPetInfo().getRow() != null) {
            return response.getVPetInfo().getRow();
        }

        return new ArrayList<>();
    }

    public List<SeoulAnimalImageApiResponse> fetchSeoulAnimalImages() {
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = String.format(
                "%s/%s/json/vPetImg/1/1000/",
                seoulApiUrl,
                seoulServiceKey
        );

        SeoulAnimalImageApiWrapper response =
                restTemplate.getForObject(requestUrl, SeoulAnimalImageApiWrapper.class);

        if (response != null
                && response.getVPetImg() != null
                && response.getVPetImg().getRow() != null) {
            return response.getVPetImg().getRow();
        }

        return new ArrayList<>();
    }
}
