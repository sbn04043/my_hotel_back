package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomFileDto;
import com.example.hotel_booking.dto.RoomTypeDto;
import com.example.hotel_booking.service.RoomFileService;
import com.example.hotel_booking.service.RoomService;
import com.example.hotel_booking.service.RoomTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final RoomFileService roomFileService;

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<?, ?>> selectOne(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.get(id));
    }

    @GetMapping("/list/{hotelId}")
    public ResponseEntity<HashMap<?, ?>> selectList(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomListByHotelId(hotelId));
    }


    @GetMapping("/write/{hotelId}")
    public ResponseEntity<RoomDto> write(@PathVariable Long hotelId) {
        return ResponseEntity.ok(RoomDto.builder().hotelId(hotelId).build());
    }

    @PostMapping("/write/{hotelId}")
    public ResponseEntity<HashMap<?, ?>> write(@PathVariable Long hotelId, @RequestBody RoomDto roomDto) {
        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            resultMap.put("result", "success");
            resultMap.put("room", roomService.insert(RoomDto.builder().hotelId(hotelId).build()));
            resultMap.put("roomTypeList", roomTypeService.selectAll());
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "fail");
        }

        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/update")
    public ResponseEntity<HashMap<?, ?>> update(@RequestBody RoomDto roomDto) {
        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            resultMap.put("result", "success");
            resultMap.put("resultRoom", roomService.update(roomDto));
            resultMap.put("roomTypeList", roomTypeService.selectAll());
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "fail");
        }
        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.delete(id));
    }

}
