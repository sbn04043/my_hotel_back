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
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("roomDto", roomService.selectOne(id));
        resultMap.put("roomTypeList", roomTypeService.selectAll());
        resultMap.put("roomFileDtoList", roomFileService.findByRoomId(id));
        resultMap.put("roomPrice", roomService.selectOne(id).getRoomPrice());

        // 호텔 아이디를 통해 userID를 빼와야함 지금은 없으니까 비교 안하고 클릭 버튼만 해놓자
        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<HashMap<?, ?>> selectList(@PathVariable Long id) {
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("roomTypeList", roomTypeService.selectAll());
        resultMap.put("roomList", roomService.selectAll(id).stream()
                .map(roomDto -> {
                    roomDto.setImageList(roomFileService.findByRoomIdToName(roomDto.getId()));
                    return roomDto;
                }).toList());

        return ResponseEntity.ok(resultMap);
    }


    @GetMapping("/write/{hotelId}")
    public ResponseEntity<RoomDto> write(@PathVariable Long hotelId) {
        return ResponseEntity.ok(RoomDto.builder().hotelId(hotelId).build());
    }

    @PostMapping("/imgInsert/{id}")
    public void insertImg(@RequestParam(value = "file", required = false) MultipartFile[] files, @PathVariable Long id, HttpServletRequest request) throws IOException {
        System.out.println("files = " + Arrays.toString(files) + ", id = " + id);

        StringBuilder fileNames = new StringBuilder();

        Path uploadPath = Paths.get("src/main/resources/static/room/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            String extension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
            }

            String storedFileName = System.currentTimeMillis() + "." + extension;
            fileNames.append(",").append(storedFileName);

            Path filePath = uploadPath.resolve(storedFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            }

            RoomFileDto temp = new RoomFileDto();
            temp.setId(id);
            temp.setOriginalFileName(originalFileName);
            temp.setStoredFileName(storedFileName);
            temp.setExtension(extension);

            roomFileService.save(temp, id);
        }
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

    @GetMapping("/roomImage")
    public ResponseEntity<Resource> getImage(@RequestParam String fileName) throws IOException {
        Path filePath = Paths.get("src/main/resources/static/room").resolve(fileName);
        if (Files.exists(filePath)) {
            Resource fileResource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .body(fileResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/showListByCondition")
    public HashMap<String, Object> selectListByDateAndPeopleCount(@RequestBody Map<String, Object> params) {
        HashMap<String, Object> resultMap = new HashMap<>();

        int hotelId = (Integer) params.get("hotelId");
        String startDateData = (String) params.get("startDate");
        String endDateData = (String) params.get("endDate");
        Integer peopleCount = (Integer) params.get("peopleCount");

        String startDate = startDateData.substring(0, 4) + startDateData.substring(5, 7) + startDateData.substring(8, 10);
        String endDate = endDateData.substring(0, 4) + endDateData.substring(5, 7) + endDateData.substring(8, 10);

        List<RoomDto> roomDtoList = roomService.selectAllByCondition(startDate, endDate, (long) hotelId, peopleCount);


        List<RoomTypeDto> roomTypeDtoList = roomTypeService.selectAll();

        for (RoomDto roomDto : roomDtoList) {
            roomDto.setImageList(roomFileService.findByRoomIdToName(roomDto.getId()));
        }


        resultMap.put("roomTypeList", roomTypeDtoList);
        resultMap.put("roomList", roomDtoList);
        resultMap.put("startDate", startDate);
        resultMap.put("endDate", endDate);
        resultMap.put("peopleCount", peopleCount);

        return resultMap;
    }
}
