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
        return ResponseEntity.ok(hotelService.selectAll().stream().map(hotelDto -> HotelDto.toHotelDto(HotelEntity.builder()
                .id(hotelDto.getId())
                .hotelName(hotelDto.getHotelName())
                .hotelAddress(hotelDto.getHotelAddress())
                .hotelPhone(hotelDto.getHotelPhone())
                .hotelEmail(hotelDto.getHotelEmail())
                .hotelGrade(hotelDto.getHotelGrade())
                .build())).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<?, ?>> selectOne(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<?, ?>> write(@RequestBody HashMap<?, ?> request) {
        return ResponseEntity.ok(hotelService.save(request));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<?, ?>> update(@RequestBody HashMap<String, Object> request, @PathVariable Long id) {
        return ResponseEntity.ok(hotelService.save(request, id));
        HotelDto hotelDto = new HotelDto();

        hotelDto.setHotelName((String) valueMap.get("hotelName"));
        hotelDto.setHotelEmail((String) valueMap.get("hotelEmail"));
        hotelDto.setHotelPhone((String) valueMap.get("hotelPhone"));
        hotelDto.setHotelAddress((String) valueMap.get("hotelAddress"));
        System.out.println(valueMap.get("hotelGrade").toString());
        int hotelGrade = Integer.parseInt(valueMap.get("hotelGrade").toString());
        int cityId = Integer.parseInt(valueMap.get("cityId").toString());

        hotelDto.setId(id);
        hotelDto.setHotelGrade((long) hotelGrade);
        hotelDto.setCityId((long) cityId);

        hotelService.update(hotelDto);

        List<FacilityDto> facilityDtoList = new ArrayList<>();

        List<Integer> facilityList = (ArrayList<Integer>) valueMap.get("facilities");
        for (int i = 0; i < facilityList.size(); i++) {
            FacilityDto temp = new FacilityDto();
            temp.setHotelId(id);
            temp.setFacilityId(facilityList.get(i).longValue());
            facilityDtoList.add(temp);
        }

        facilityService.update(facilityDtoList, id);
        System.out.println(facilityList);

        System.out.println("HotelController.update");

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", hotelDto);
        resultMap.put("resultId", id);

        return ResponseEntity.ok(resultMap);

    }


    @GetMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.delete(id));
    }

    @PostMapping("/imgInsert/{id}")
    public ResponseEntity<Boolean> insertImg(@RequestParam(value = "file", required = false) MultipartFile[] files, @RequestParam Long id) throws IOException {
        System.out.println("HotelController.insertImg");
        System.out.println("files = " + Arrays.toString(files) + ", id = " + id);

        if (files == null || files.length == 0) {
            return ResponseEntity.ok(false);
        }

        StringBuilder fileNames = new StringBuilder();

        Path uploadPath = Paths.get("src/main/resources/static/hotel");
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

            HotelFileDto temp = new HotelFileDto();
            temp.setId(id);
            temp.setOriginalFileName(originalFileName);
            temp.setStoredFileName(storedFileName);
            temp.setExtension(extension);

            System.out.println(temp);

            hotelFileService.save(temp, id);
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/image")
    public ResponseEntity<Resource> getImage(@RequestParam String fileName) throws IOException {
        Path filePath = Paths.get("src/main/resources/static/hotel").resolve(fileName);
        if (Files.exists(filePath)) {
            Resource fileResource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .body(fileResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}