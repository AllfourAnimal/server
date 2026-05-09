package com.All4Animal.server.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AdoptionStatusConverter implements AttributeConverter<Adoption.AdoptionStatus, String> {

    @Override
    public String convertToDatabaseColumn(Adoption.AdoptionStatus status) {
        return status == null ? null : status.name();
    }

    @Override
    public Adoption.AdoptionStatus convertToEntityAttribute(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        if ("APPLIED".equals(status)) {
            return Adoption.AdoptionStatus.INQUIRY;
        }
        return Adoption.AdoptionStatus.valueOf(status);
    }
}
