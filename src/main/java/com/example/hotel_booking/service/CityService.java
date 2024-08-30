package com.example.hotel_booking.service;

import com.example.hotel_booking.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
}
