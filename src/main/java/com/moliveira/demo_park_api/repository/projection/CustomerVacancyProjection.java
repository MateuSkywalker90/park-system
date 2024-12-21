package com.moliveira.demo_park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CustomerVacancyProjection {

     String getLicensePlate();

     String getBrand();

     String getModel();

     String getColor();

     String getClientCpf();

     String getReceipt();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
     LocalDateTime getEntryDate();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
     LocalDateTime getDepartureDate();

     String getParkingSpaceCode();

     BigDecimal getAmount();

     BigDecimal getDiscount();

}
