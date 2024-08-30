package com.example.hotel_booking.repository;

import com.example.hotel_booking.entity.HotelFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelFacilityRepository extends JpaRepository<HotelFacilityEntity, Long> {
    @Query(value = "SELECT facility_id FROM hotel_facility WHERE hotel_id = ?1", nativeQuery = true)
    List<Long> findByHotelEntity_id(Long id);

    @Modifying
    @Query(value = "DELETE FROM hotel_facility (SELECT hotel_id FROM (SELECT * FROM hotel_facility WHERE hotel_id = ?1))", nativeQuery = true)
    void deleteAllByHotelId(Long id);
}