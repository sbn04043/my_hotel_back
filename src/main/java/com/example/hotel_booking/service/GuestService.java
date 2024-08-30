package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.UserDto;
import com.example.hotel_booking.entity.UserEntity;
import com.example.hotel_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GuestService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserDto findById(Long id) {
        Optional<UserEntity> guestEntityOptional = userRepository.findById(id);
        if (guestEntityOptional.isPresent()) {
            UserEntity guestEntity = guestEntityOptional.get();
            UserDto guestDto = UserDto.toGuestDto(guestEntity);
            return guestDto;
        } else {
            return null;
        }
    }

    public UserDto update(UserDto guestDto) {
        userRepository.save(UserEntity.toGuestEntity(guestDto));

        return findById(guestDto.getId());
    }

    public Boolean findByEmailAndNameAndPhone(Map<?, ?> map) {
        return userRepository.findByEmailAndNameAndPhone(map.get("email").toString(), map.get("name").toString(), map.get("phone").toString()).isPresent();
    }

    public UserDto findByNameAndPhone(Map<?, ?> map) {
        return UserDto.toUserDto(userRepository.findByNameAndPhone(map.get("name").toString(), map.get("phone").toString()).get());
    }


    public UserDto auth(Map<?, ?> map) {
        if (userRepository.findByEmail(map.get("email").toString()).get().getPassword().equals(map.get("password").toString())) {
            return UserDto.toGuestDto(userRepository.findByEmail(map.get("email").toString()).get());
        }
        return null;
    }

    public boolean checkPassword(Long userId, String password) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // 비밀번호를 암호화 체크 해야함
            return user.getPassword().equals(password);
        } else {
            return false; // 사용자가 존재하지 않는 경우
        }
    }
}
