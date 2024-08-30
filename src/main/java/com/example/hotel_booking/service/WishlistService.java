package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.WishlistDto;
import com.example.hotel_booking.entity.ReservationEntity;
import com.example.hotel_booking.entity.WishlistEntity;
import com.example.hotel_booking.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }


    public List<WishlistDto> findAllByGuestId(Long id) {
        List<WishlistEntity> wishlistEntityList = wishlistRepository.findAllByGuestId(id);
        List<WishlistDto> wishlistDtoList = new ArrayList<>();
        for (WishlistEntity wishlistEntity : wishlistEntityList) {
            wishlistDtoList.add(WishlistDto.toWishlistDto(wishlistEntity));
        }

        return wishlistDtoList;
    }

    public Boolean wishAddDelete(WishlistDto wishlistDto) {
        if (wishlistRepository.isExist(wishlistDto.getGuestId(), wishlistDto.getHotelId()) != null) {
            wishlistRepository.delete(WishlistEntity.toWishlistEntity(wishlistDto));
            return true;
        } else {
            wishlistRepository.save(WishlistEntity.toWishlistEntity(wishlistDto));
            return false;
        }
    }
}
