package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.UserDto;
import com.example.hotel_booking.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @RequestMapping("authOk")
    public ResponseEntity<Map<String, Object>> authOk(@RequestBody UserDto userDto) {
        Map<String, Object> resultMap = new HashMap<>();
        // getPrincipal을 바로 resultMap에 넣는건 좋지 않다 왜냐면 패스워드가 넘어가니깐
        resultMap.put("result", "success");
        resultMap.put("id", userDto.getId());
        resultMap.put("nickname", userDto.getNickname());
        resultMap.put("role", userDto.getRole());

        return ResponseEntity.ok(resultMap);
    }

    @RequestMapping("authFail")
    public ResponseEntity<Map<String, Object>> authFail() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", "fail");

        return ResponseEntity.ok(resultMap);
    }

    @RequestMapping("logOutSuccess")
    public ResponseEntity<Void> logOutSuccess() {
        System.out.println("log out success");

        //위에선 resultMap을 통해서 ok 했지만 로그아웃은 아무것도 없으므로 어쩌라고 듣지 않기 위해 .build()를 해준다.
        return ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public HashMap<String, Object> register(@RequestBody UserDto userDto) {
        System.out.println(userDto);
        userDto.setPassword(userDto.getPassword());
        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            userService.register(userDto);
            resultMap.put("result", "success");
            resultMap.put("resultId", userDto.getId());

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "fail");
        }
        return resultMap;
    }

}
