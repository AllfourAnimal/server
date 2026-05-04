package com.All4Animal.server.dto.response.api;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class AnimalApiResponse {

    private String desertionNo;

    private String upKindNm;

    private String popfile1;

    private String popfile2;

    private String happenPlace;

    private String kindNm;

    private String colorCd;

    private String age;

    private String weight;

    private String sexCd;

    private String neuterYn;

    private String specialMark;

    private String careNm;

    private String careTel;

    private String careAddr;

    private String processState;
}
