package com.All4Animal.server.dto.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SeoulAnimalImageApiWrapper {

    @JsonProperty("vPetImg")
    private SeoulAnimalImageApiBody vPetImg;

    @Getter
    @NoArgsConstructor
    public static class SeoulAnimalImageApiBody {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Object result;

        @JsonProperty("row")
        private List<SeoulAnimalImageApiResponse> row;
    }
}
