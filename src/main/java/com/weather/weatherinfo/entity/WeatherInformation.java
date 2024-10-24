package com.weather.weatherinfo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeatherInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pincode;
    private String  place;
    private LocalDate date;
    private double temperature;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private String  description;
    private double temp_min;
    private double temp_max;
    private String country;
    private String name;


}
