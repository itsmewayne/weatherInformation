package com.weather.weatherinfo.repositories;

import com.weather.weatherinfo.entity.PincodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PincodeDataRepository extends JpaRepository<PincodeData,Long> {

    Optional<PincodeData> findByPincode(String pincode);
}
