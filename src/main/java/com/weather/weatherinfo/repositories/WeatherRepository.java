package com.weather.weatherinfo.repositories;

import com.weather.weatherinfo.dto.WeatherResponse;
import com.weather.weatherinfo.entity.PincodeData;
import com.weather.weatherinfo.entity.WeatherInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherInformation,Long> {
    Optional<WeatherInformation> findByPincodeAndDate(String pincode, LocalDate date);

}
