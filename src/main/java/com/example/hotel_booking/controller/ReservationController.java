package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.ReservationDto;
import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomFileDto;
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

    @GetMapping("/{roomId}}")
    public ResponseEntity<Map<?, ?>> selectOne(@PathVariable Long roomId) {
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("reservationDto", reservationService.selectOne(roomId));
        resultMap.put("roomDto", roomService.selectOne(reservationService.selectOne(roomId).getRoomId()));
        resultMap.put("roomTypeList", roomTypeService.selectAll());
        resultMap.put("roomFileDtoList", roomFileService.findByRoomId(reservationService.selectOne(roomId).getRoomId()));

        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<HashMap<?, ?>> save(@PathVariable Long roomId, @RequestBody ReservationDto reservationDto) {
        reservationDto.setRoomId(roomId);
        // 유저 정보는 가져와야하니까 로그인된 사람이 예약눌렀을때 로그인된 아이디의 id값을 출력해야함
        reservationDto.setUserId(1L);
        String reservationNum = String.valueOf(System.currentTimeMillis());
        reservationDto.setReservationNumber(reservationNum);
        // 가격은 계산 나중에 다시 설정
        // 방 가격
        // endDate-startDate= 2   얘네를 스트링으로 받아와서 인티저로 바꿔 그다음에
        reservationDto.setPayPrice(roomService.selectOne(roomId).getRoomPrice() * 2);
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            Long reservationId = reservationService.insert(reservationDto);

            resultMap.put("result", "success");
            resultMap.put("reservationId", reservationId);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "fail");
        }
        return ResponseEntity.ok(resultMap);
    }
}

