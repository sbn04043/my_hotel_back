package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.*;
import com.example.hotel_booking.entity.UserEntity;
import com.example.hotel_booking.service.GuestService;
import com.example.hotel_booking.service.ReservationService;
import com.example.hotel_booking.service.ReviewService;
import com.example.hotel_booking.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/guests")
public class GuestController {
    private final GuestService guestService;
    private final ReservationService reservationService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;

    //id로 유저 정보 가져오기(내 정보)
    @GetMapping("/info/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.findById(id));
    }

    //유저 업데이트(수정 페이지로)
    @PostMapping("/")
    public ResponseEntity<UserDto> putUser(@RequestBody UserDto guestDto) {
        return ResponseEntity.ok(guestService.update(guestDto));
    }

    //예약 정보 출력(내 예약)
    @GetMapping("/myReservation/{id}")
    public ResponseEntity<List<ReservationDto>> reservations(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.findAllByGuestId(id));
    }

    // 좋아하는 호텔 유저 id를 통해 가져오기(내 찜 목록)
    @GetMapping("/myWishlist/{id}")
    public ResponseEntity<List<WishlistDto>> wishlist(@PathVariable Long id) {
        return ResponseEntity.ok(wishlistService.findAllByGuestId(id));
    }

    // 좋아하는 호텔 저장
    @PostMapping("/wishlist")
    public ResponseEntity<Boolean> wishAddDelete(@RequestBody WishlistDto wishlistDto) {
        return ResponseEntity.ok(wishlistService.wishAddDelete(wishlistDto));
    }

    //리뷰 불러오기
    @GetMapping("/review/{userId}")
    public ResponseEntity<List<ReviewDto>> review(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.findAllByGuestId(userId));
    }

    //리뷰 수정 페이지로 갈 때 리뷰 정보 가져오기
    @GetMapping("/review")
    public ResponseEntity<ReviewDto> reviewPage() {
        return ResponseEntity.ok(new ReviewDto());
    }

    // 리뷰 추가 API
    @PostMapping("/review")
    public ResponseEntity<Boolean> reviewAdd(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.save(reviewDto));
    }

    //리뷰 수정 페이지로
    @GetMapping("/review/update/{id}")
    public ResponseEntity<ReviewDto> reviewUpdatePage(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    //리뷰 업데이트 API
    @PutMapping("/review/update")
    public ResponseEntity<Boolean> reviewUpdate(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.reviewUpdate(reviewDto));    }

    //리뷰 삭제 API
    @DeleteMapping("/review/{id}")
    public ResponseEntity<Boolean> reviewDelete(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.deleteReview(id));
    }

    //이메일 찾기
    @PostMapping("/findUsername")
    public ResponseEntity<UserDto> findUsername(@RequestBody Map<?, ?> request) {
        return ResponseEntity.ok(guestService.findByNameAndPhone(request));
    }

    //비밀번호 찾기
    @PostMapping("/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(guestService.findByEmailAndNameAndPhone(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<UserDto> auth(@RequestBody Map<?, ?> request) {
        return ResponseEntity.ok(guestService.auth(request));
    }

    @PostMapping("/check-password")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@RequestBody PasswordCheckRequest request) {
        boolean valid = guestService.checkPassword(request.getUserId(), request.getPassword());
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);
        return ResponseEntity.ok(response);
    }
}
