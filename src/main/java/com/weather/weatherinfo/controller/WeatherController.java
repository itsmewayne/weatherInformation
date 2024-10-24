package com.weather.weatherinfo.controller;

import com.weather.weatherinfo.entity.WeatherInformation;
import com.weather.weatherinfo.services.implementation.WeatherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WeatherController {


    private final WeatherServiceImpl weatherService;


    @GetMapping("/weatherInfo")
    public ResponseEntity<?> getInfo(@RequestParam String pincode,
                                                      @RequestParam LocalDate for_date)
    {
        WeatherInformation weatherInformation = weatherService.getWeatherInformation(pincode, for_date);
        if (weatherInformation!=null)
        {
            return new ResponseEntity<>(weatherInformation,HttpStatus.OK);
        }
        return new ResponseEntity<>("Getting Some Error",HttpStatus.NOT_FOUND);
    }

}
