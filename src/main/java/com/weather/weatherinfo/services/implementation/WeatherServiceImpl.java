package com.weather.weatherinfo.services.implementation;

import com.weather.weatherinfo.dto.Coord;
import com.weather.weatherinfo.dto.WeatherResponse;
import com.weather.weatherinfo.entity.PincodeData;
import com.weather.weatherinfo.entity.WeatherInformation;
import com.weather.weatherinfo.exception.CustomException;
import com.weather.weatherinfo.repositories.PincodeDataRepository;
import com.weather.weatherinfo.repositories.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl {
    private final WeatherRepository weatherRepository;
    private final PincodeDataRepository pincodeDataRepository;
    private final RestTemplate restTemplate;

    @Value("${open.weather.api_key}")
    private String openWeatherApiKey;

    public WeatherInformation getWeatherInformation(String pincode, LocalDate forDate) {
        try {
            Optional<WeatherInformation> cachedWeather = weatherRepository.findByPincodeAndDate(pincode, forDate);
            if (cachedWeather.isPresent()) {
                return cachedWeather.get();
            }

            PincodeData pincodeData = getPincodeDataWithCache(pincode);
            WeatherResponse weatherApiResponse = getWeatherResponse(pincodeData.getLatitude(), pincodeData.getLongitude(), forDate);

            WeatherInformation weatherInformation = buildWeatherInformation(weatherApiResponse, pincode, forDate);
            return weatherRepository.save(weatherInformation);
        } catch (Exception e) {
            throw new CustomException("Failed to fetch weather information: " + e.getMessage());
        }
    }

    public PincodeData getPincodeDataWithCache(String pincode) {
        try {
            return pincodeDataRepository.findByPincode(pincode)
                    .orElseGet(() -> fetchAndSavePincodeData(pincode));
        } catch (DataAccessException e) {
            throw new CustomException("Database error while fetching pincode data: " + e.getMessage());
        }
    }

    public PincodeData fetchAndSavePincodeData(String pincode) {
        try {
            String country = pincode.length() == 6 ? "IN" : "US";
            String url = String.format("https://api.openweathermap.org/data/2.5/weather?zip=%s,%s&appid=%s",
                    pincode, country, openWeatherApiKey);

            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new CustomException("Failed to fetch coordinates for pincode: " + pincode);
            }
            Coord coord = response.getBody().getCoord();
            PincodeData pincodeData = PincodeData.builder()
                    .pincode(pincode)
                    .latitude(coord.getLat())
                    .longitude(coord.getLon())
                    .build();
            return pincodeDataRepository.save(pincodeData);
        } catch (Exception e) {
            throw new CustomException("Error fetching pincode data: " + e.getMessage());
        }
    }

    private WeatherResponse getWeatherResponse(Double latitude, Double longitude, LocalDate forDate) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&dt=%s",
                latitude, longitude, openWeatherApiKey, forDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond());

        ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new CustomException("Failed to fetch weather data");
        }

        return response.getBody();
    }

    private WeatherInformation buildWeatherInformation(WeatherResponse response, String pincode, LocalDate forDate) {
        return WeatherInformation.builder()
                .place(response.getName())
                .temp_max(response.getMain().getTemp_max())
                .temp_min(response.getMain().getTemp_min())
                .temperature(response.getMain().getTemp())
                .windSpeed(response.getWind().getSpeed())
                .description(response.getWeather().get(0).getDescription())
                .pressure(response.getMain().getPressure())
                .date(forDate)
                .country(response.getSys().getCountry())
                .humidity(response.getMain().getHumidity())
                .pincode(pincode)
                .name(response.getName())
                .build();
    }
}