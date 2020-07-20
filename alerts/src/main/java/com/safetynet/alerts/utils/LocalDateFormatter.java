package com.safetynet.alerts.utils;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateFormatter {

    /**
     * Localdate to String with Pattern
     * @param localdate localdate
     * @param pattern pattern like dd-mm-yyyy
     * @return String
     */
    public static String convertToString(@NonNull LocalDate localdate, @Nullable String pattern){
        if(pattern == null){
            return localdate.format(DateTimeFormatter.ofPattern("MM-dd-YYYY"));}
        else {
            return localdate.format(DateTimeFormatter.ofPattern(pattern));
        }
    }
}


//4.2 https://www.baeldung.com/java-avoid-null-check