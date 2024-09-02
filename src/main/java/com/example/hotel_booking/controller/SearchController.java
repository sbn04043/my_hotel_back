package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.dto.HotelFileDto;
import com.example.hotel_booking.dto.TestDto;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.service.HotelFileService;
import com.example.hotel_booking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final HotelService hotelService;

    //호텔 찾기
    @PostMapping("/hotel")
    public ResponseEntity<Map<?, ?>> searchHotel(@RequestBody Map<String, Object> data) {
        List<Integer> gradeIntegerList = (List<Integer>) data.get("grade");
        List<Integer> cityIdIntegerList = (List<Integer>) data.get("cityId");
        List<Integer> facilityIdIntegerList = (List<Integer>) data.get("facilityId");
        List<Integer> roomTypeIdIntegerList = (List<Integer>) data.get("roomTypeId");
        String hotelName = (String) data.get("hotelName");

        List<Long> gradeList = new ArrayList<>();
        if (gradeIntegerList != null) {
            for (int gradeInteger : gradeIntegerList) {
                gradeList.add((long) gradeInteger);
            }
        }

        List<Long> cityIdList = new ArrayList<>();
        if (cityIdIntegerList != null) {
            for (int cityIdInteger : cityIdIntegerList) {
                cityIdList.add((long) cityIdInteger);
            }
        }

        List<Long> facilityIdList = new ArrayList<>();
        if (facilityIdIntegerList != null) {
            for (int facilityIdInteger : facilityIdIntegerList) {
                facilityIdList.add((long) facilityIdInteger);
            }
        }

        List<Long> roomTypeIdList = new ArrayList<>();
        if (roomTypeIdIntegerList != null) {
            for (int roomTypeIdInteger : roomTypeIdIntegerList) {
                roomTypeIdList.add((long) roomTypeIdInteger);
            }
        }

        List<HotelDto> hotelDtoList = hotelService.searchHotel(gradeList, cityIdList, facilityIdList, hotelName);

        String startDateData = data.get("startDate").toString();
        String endDateData = data.get("endDate").toString();
        String startDate = "";
        String endDate = "";

        if (!startDateData.isEmpty() && !endDateData.isEmpty()) {
            startDate = startDateData.substring(0, 4) + startDateData.substring(5, 7) + startDateData.substring(8, 10);
            startDate = (Integer.parseInt(startDate) + 1) + "";
            endDate = endDateData.substring(0, 4) + endDateData.substring(5, 7) + endDateData.substring(8, 10);

            if (Integer.parseInt(startDate) > Integer.parseInt(endDate)) {
                startDate = "";
                endDate = "";
            }
        }

        int peopleCount = Integer.parseInt(data.get("peopleCount").toString());
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("hotelDtoList", hotelDtoList);
        resultMap.put("startDate", startDate);
        resultMap.put("endDate", endDate);
        resultMap.put("peopleCount", peopleCount);

        return ResponseEntity.ok(resultMap) ;
    }

}


