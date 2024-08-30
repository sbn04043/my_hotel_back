package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.ReviewDto;
import com.example.hotel_booking.entity.ReviewEntity;
import com.example.hotel_booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    //user id로 리뷰 모두 가져오기
    public List<ReviewDto> findAllByGuestId(Long id) {
        List<ReviewEntity> reviewEntityOptional = reviewRepository.findAllByGuestId(id);
        List<ReviewDto> reviewDtoList = new ArrayList<>();

        for (ReviewEntity reviewEntity : reviewEntityOptional) {
            ReviewDto reviewDto = ReviewDto.toReviewDto(reviewEntity);
            reviewDtoList.add(reviewDto);
        }

        return reviewDtoList;
    }

    public ReviewDto findById(Long id) {
        ReviewEntity reviewEntity = reviewRepository.findById(id).orElse(null);
        return reviewEntity != null ? ReviewDto.toReviewDto(reviewEntity) : null;
    }

    public Boolean reviewUpdate(ReviewDto reviewDto) {
        if (reviewRepository.existsById(reviewDto.getId())) {
            reviewRepository.save(ReviewEntity.toUpdateReviewEntity(reviewDto));
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Boolean save(ReviewDto reviewDto) {
        ReviewEntity reviewEntity = ReviewEntity.toAddReviewEntity(reviewDto);
        if (reviewEntity.getId() == null) {
            return false;
        } else {
            reviewRepository.save(reviewEntity);
            return true;
        }
    }
}
