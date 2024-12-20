package com.moliveira.demo_park_api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {

    // LocalDate format 2024-12-20T18:53:48.616463500
    // Receipt format 20241220-185348
    public static String createReceipt() {
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0,19);
        return receipt.replace("-","")
                .replace(":","")
                .replace("T","-");
    }
}
