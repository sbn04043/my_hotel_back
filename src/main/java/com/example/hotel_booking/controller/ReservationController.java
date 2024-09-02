package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.ReservationDto;
import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomFileDto;
import com.example.hotel_booking.entity.ReservationEntity;
import com.example.hotel_booking.repository.RoomRepository;
import com.example.hotel_booking.repository.UserRepository;
import com.example.hotel_booking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hotel_booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;
    private final RoomService roomService;
    private final RoomFileService roomFileService;
    private final RoomTypeService roomTypeService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @GetMapping("/{roomId}")
    public ResponseEntity<Map<?, ?>> selectOne(@PathVariable Long roomId) {
        return ResponseEntity.ok(reservationService.selectOne(roomId));
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<ReservationDto> save(@PathVariable Long roomId, @RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.save(ReservationEntity.builder()
                .roomEntity(roomRepository.findById(roomId).get())
                .guestEntity(userRepository.findById(1L).get())
                .startDate(reservationDto.getStartDate())
                .endDate(reservationDto.getEndDate())
                .payPrice(reservationDto.getPayPrice())
                .isBreakfast(reservationDto.getIsBreakfast())
                .reservationNumber(String.valueOf(System.currentTimeMillis()))
                .build()));
    }
}

