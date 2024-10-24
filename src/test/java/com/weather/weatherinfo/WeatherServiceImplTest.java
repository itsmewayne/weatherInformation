package com.weather.weatherinfo;

import com.weather.weatherinfo.dto.*;
import com.weather.weatherinfo.entity.PincodeData;
import com.weather.weatherinfo.entity.WeatherInformation;
import com.weather.weatherinfo.repositories.PincodeDataRepository;
import com.weather.weatherinfo.repositories.WeatherRepository;
import com.weather.weatherinfo.services.implementation.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceImplTest {


    @Mock
    private  WeatherRepository weatherRepository;
    @Mock
    private  PincodeDataRepository pincodeDataRepository;
    @Mock
    private  RestTemplate restTemplate;
    @InjectMocks
    private WeatherServiceImpl weatherService;
    private WeatherResponse mockWeatherResponse;
    private PincodeData mockPincodeData;
    private WeatherInformation mockWeatherInfo;

    String TEST_PINCODE="411014";
    LocalDate TEST_DATE=LocalDate.now();
    @BeforeEach
    void setUp() {
        mockWeatherResponse = new WeatherResponse();
        mockWeatherResponse.setName("Test City");
        mockWeatherResponse.setCoord(new Coord(12.34, 56.78));

        Main main = new Main();
        main.setTemp(20.0);
        main.setTemp_max(25.0);
        main.setTemp_min(15.0);
        main.setHumidity(65);
        main.setPressure(1013);
        mockWeatherResponse.setMain(main);

        Weather weather = new Weather();
        weather.setDescription("Clear sky");
        mockWeatherResponse.setWeather(Collections.singletonList(weather));

        Wind wind = new Wind();
        wind.setSpeed(5.0);
        mockWeatherResponse.setWind(wind);

        Sys sys = new Sys();
        sys.setCountry("IN");
        mockWeatherResponse.setSys(sys);

        mockPincodeData = PincodeData.builder()
                .pincode(TEST_PINCODE)
                .latitude(12.34)
                .longitude(56.78)
                .build();

        mockWeatherInfo = WeatherInformation.builder()
                .pincode(TEST_PINCODE)
                .date(TEST_DATE)
                .temperature(20.0)
                .temp_max(25.0)
                .temp_min(15.0)
                .humidity(65)
                .pressure(1013)
                .description("Clear sky")
                .windSpeed(5.0)
                .country("IN")
                .name("TestCity")
                .build();
    }



    @Test
    void whenWeatherDataIsCached_returnsCachedData() {

        when(weatherRepository.findByPincodeAndDate(TEST_PINCODE, TEST_DATE))
                .thenReturn(Optional.of(mockWeatherInfo));

        // When
        WeatherInformation result = weatherService.getWeatherInformation(TEST_PINCODE, TEST_DATE);

        // Then
        assertNotNull(result);
        assertEquals(TEST_PINCODE, result.getPincode());
        assertEquals("TestCity", result.getName());
        assertEquals(mockWeatherInfo,result);
        verify(pincodeDataRepository,never()).findByPincode(TEST_PINCODE);
        verify(restTemplate,never()).getForEntity(anyString(),any());
    }
    @Test
    void getPincodeDataWhenExists_returnCacheData()
    {
        //Given
        when(pincodeDataRepository.findByPincode(TEST_PINCODE))
                .thenReturn(Optional.of(mockPincodeData));
        PincodeData result = weatherService.getPincodeDataWithCache(TEST_PINCODE);
        assertNotNull(result);
        assertEquals(TEST_PINCODE, result.getPincode());
        assertEquals(12.34, result.getLatitude());
        assertEquals(56.78, result.getLongitude());
        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

}
