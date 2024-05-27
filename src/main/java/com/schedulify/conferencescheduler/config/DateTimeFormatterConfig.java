package com.schedulify.conferencescheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeFormatterConfig {

    @Value("${time.format.pattern}")
    private String timeFormatPattern;

    @Bean
    public DateTimeFormatter timeFormatter() {
        return DateTimeFormatter.ofPattern(timeFormatPattern);
    }
}
