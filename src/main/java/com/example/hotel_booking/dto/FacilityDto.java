package com.example.hotel_booking.dto;

import com.example.hotel_booking.entity.FacilityEntity;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.entity.HotelFacilityEntity;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.example.hotel_booking.entity.FacilityEntity}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDto implements Serializable {
    private Long id;
    private Long facilityId;
    private Long hotelId;

    public static FacilityDto toFacilityDto(HotelFacilityEntity hotelFacilityEntity, Long hotelId){
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setId(hotelFacilityEntity.getId());
        facilityDto.setHotelId(hotelId);
        facilityDto.setFacilityId(hotelFacilityEntity.getFacilityId());
        return facilityDto;
    }
}