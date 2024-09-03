package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.FacilityDto;
import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.dto.HotelFileDto;
import com.example.hotel_booking.entity.FacilityEntity;
import com.example.hotel_booking.entity.HotelFacilityEntity;
import com.example.hotel_booking.repository.CityRepository;
import com.example.hotel_booking.repository.HotelFacilityRepository;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.service.*;
import com.example.hotel_booking.entity.HotelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.MulticastChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@CrossOrigin
public class HotelController {
    private final HotelService hotelService;
    private final FacilityService facilityService;
    private final HotelFileService hotelFileService;
    private final WishlistService wishlistService;
    private final CityRepository cityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final HotelRepository hotelRepository;

    @GetMapping("/list")
    public ResponseEntity<List<HotelDto>> hotelList() {
        return ResponseEntity.ok(hotelService.selectAll().stream().map(hotelDto ->
                HotelDto.toHotelDto(HotelEntity.builder()
                        .id(hotelDto.getId())
                        .hotelName(hotelDto.getHotelName())
                        .hotelAddress(hotelDto.getHotelAddress())
                        .hotelPhone(hotelDto.getHotelPhone())
                        .hotelEmail(hotelDto.getHotelEmail())
                        .hotelGrade(hotelDto.getHotelGrade())
                        .build())).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> selectOne(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<?, ?>> write(@RequestBody HashMap<?, ?> request) {
        return ResponseEntity.ok(hotelService.save(request));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<?, ?>> update(@RequestBody HashMap<String, Object> request, @PathVariable Long id) {
        return ResponseEntity.ok(hotelService.save(request, id));
    }


    @GetMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.delete(id));
    }
}