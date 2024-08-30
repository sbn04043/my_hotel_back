package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.FacilityDto;
import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.entity.CityEntity;
import com.example.hotel_booking.entity.FacilityEntity;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.entity.HotelFacilityEntity;
import com.example.hotel_booking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HotelService {
    private final HotelRepository hotelRepository;
    private final FacilityRepository facilityRepository;
    private final CityRepository cityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final HotelFileService hotelFileService;

    public List<HotelDto> searchHotel(List<Long> gradeList, List<Long> cityIdList
            , List<Long> facilityIdList, String hotelName) {

        List<HotelDto> hotelDtoList = new ArrayList<>();

        List<Long> hotelIdListFindByGrade = new ArrayList<>();
        if (!gradeList.isEmpty()) {
            for (Long grade : gradeList) {
                hotelIdListFindByGrade.addAll(hotelRepository.findByGrade(grade));
            }
        } else {
            hotelIdListFindByGrade = hotelRepository.findAllId();
        }

        if (hotelIdListFindByGrade.isEmpty()) {
            return null;
        }
        Collections.sort(hotelIdListFindByGrade);

        List<Long> hotelIdListFindByCity = new ArrayList<>();
        if (!cityIdList.isEmpty()) {
            for (Long cityId : cityIdList) {
                hotelIdListFindByCity.addAll(hotelRepository.findByCityId(cityId));
            }
        } else {
            hotelIdListFindByCity = hotelRepository.findAllId();
        }

        if (hotelIdListFindByCity.isEmpty()) {
            return null;
        }
        Collections.sort(hotelIdListFindByCity);

        List<Long> hotelIdListFindByFacility = new ArrayList<>();
        if (!facilityIdList.isEmpty()) {
            for (Long facilityId : facilityIdList) {
                hotelIdListFindByFacility.addAll(facilityRepository.findAllByFacilityId(facilityId));
            }

            //key: hotel id, value: 나온 갯수
            Map<Long, Integer> hotelIdCount = new HashMap<>();
            for (Long hotelId : hotelIdListFindByFacility) {
                if (hotelIdCount.containsKey(hotelId)) {
                    hotelIdCount.put(hotelId, hotelIdCount.get(hotelId) + 1);
                } else {
                    hotelIdCount.put(hotelId, 1);
                }
            }

            for (Map.Entry<Long, Integer> entry : hotelIdCount.entrySet()) {
                if (entry.getValue() >= facilityIdList.size()) {
                    hotelIdListFindByFacility.add(entry.getKey());
                }
            }
        } else {
            hotelIdListFindByFacility = hotelRepository.findAllId();
        }

        if (hotelIdListFindByFacility.isEmpty()) {
            return null;
        }
        Collections.sort(hotelIdListFindByFacility);

        //Set<Long> roomTypeIdSet = new HashSet<>();

        List<Long> hotelIdListFindByHotelName;
        if (hotelName != null) {
            hotelIdListFindByHotelName = new ArrayList<>(hotelRepository.findByHotelNameContaining(hotelName));
        } else {
            hotelIdListFindByHotelName = hotelRepository.findAllId();
        }

        if (hotelIdListFindByHotelName.isEmpty()) {
            return null;
        }
        Collections.sort(hotelIdListFindByHotelName);

        int gradeIndex = 0, cityIndex = 0, facilityIndex = 0, hotelIndex = 0;

        while (gradeIndex < hotelIdListFindByGrade.size() &&
                cityIndex < hotelIdListFindByCity.size() &&
                facilityIndex < hotelIdListFindByFacility.size() &&
                hotelIndex < hotelIdListFindByHotelName.size()) {

            long gradeCurVal = hotelIdListFindByGrade.get(gradeIndex);
            long cityCurVal = hotelIdListFindByCity.get(cityIndex);
            long facilityCurVal = hotelIdListFindByFacility.get(facilityIndex);
            long hotelCurVal = hotelIdListFindByHotelName.get(hotelIndex);

            if (gradeCurVal == cityCurVal &&
                    cityCurVal == facilityCurVal &&
                    facilityCurVal == hotelCurVal) {
                HotelDto tempHotelDto = HotelDto.toHotelDto(hotelRepository.findById(gradeCurVal));

                hotelDtoList.add(tempHotelDto);
                gradeIndex++;
                cityIndex++;
                facilityIndex++;
                hotelIndex++;
            } else {
                long tempMin1 = Math.min(gradeCurVal, cityCurVal);
                long tempMin2 = Math.min(facilityCurVal, hotelCurVal);
                long minVal = Math.min(tempMin1, tempMin2);

                if (gradeCurVal == minVal)
                    gradeIndex++;
                if (cityCurVal == minVal)
                    cityIndex++;
                if (facilityCurVal == minVal)
                    facilityIndex++;
                if (hotelCurVal == minVal)
                    hotelIndex++;
            }
        }

        System.out.println("gradeList: " + hotelIdListFindByGrade);
        System.out.println("cityList: " + hotelIdListFindByCity);
        System.out.println("facilityList: " + hotelIdListFindByFacility);
        System.out.println("nameList: " + hotelIdListFindByHotelName);
        System.out.println(facilityIdList);
//        for (HotelDto hotelDto : hotelDtoList) {
//            System.out.println(hotelDto.toString());
//        }
        return hotelDtoList;
    }

    public Map<?, ?> save(Map<?, ?> request, Long id) {
        HotelDto hotelDto = HotelDto.toHotelDto(hotelRepository.save(HotelEntity.builder()
                .id(id)
                .hotelName(request.get("hotelName").toString())
                .hotelEmail(request.get("hotelEmail").toString())
                .hotelPhone(request.get("hotelPhone").toString())
                .hotelAddress(request.get("hotelAddress").toString())
                .hotelGrade(Long.parseLong(request.get("hotelGrade").toString()))
                .cityEntity(cityRepository.findById(Long.parseLong(request.get("cityId").toString())).get())
                .build()));

        hotelFacilityRepository.deleteAllByHotelId(id);
        List<Long> facilityIdList = hotelFacilityRepository.findByHotelEntity_id(hotelDto.getId());

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", hotelDto);
        resultMap.put("facilityIdList", facilityIdList);
        return resultMap;
    }

    public Map<?, ?> save(Map<?, ?> request) {
        HotelDto hotelDto = HotelDto.toHotelDto(hotelRepository.save(HotelEntity.builder()
                .hotelName(request.get("hotelName").toString())
                .hotelEmail(request.get("hotelEmail").toString())
                .hotelPhone(request.get("hotelPhone").toString())
                .hotelAddress(request.get("hotelAddress").toString())
                .hotelGrade(Long.parseLong(request.get("hotelGrade").toString()))
                .cityEntity(cityRepository.findById(Long.parseLong(request.get("cityId").toString())).get())
                .build()));

        //List<Long> facilityIdList = hotelFacilityRepository.save(hotelDto.getId(), );

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", hotelDto);
        //resultMap.put("facilityIdList", facilityIdList);
        return resultMap;
    }


    @Transactional
    public Boolean delete(Long id) {
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<HotelDto> selectAll() {
        List<HotelEntity> hotelEntityList = hotelRepository.findAll();
        List<HotelDto> hotelDtoList = new ArrayList<>();
        for (HotelEntity hotelEntity : hotelEntityList) {
            hotelDtoList.add(HotelDto.toAllHotelDto(hotelEntity));
        }
        return hotelDtoList;
    }

    public Map<?, ?> findById(long id) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("hotelDto", findById(id));
        hashMap.put("facilities", facilityAll(id).stream().distinct().collect(Collectors.toList()));
        hashMap.put("hotelFileDtoList", hotelFileService.findByHotelId(id));

        return hashMap;
    }

    public HotelDto update(HotelDto hotelDto) {
        CityEntity cityEntity = cityRepository.findById(hotelDto.getCityId()).get();
        HotelEntity hotelEntity = HotelEntity.updateHotelEntity(hotelDto, cityEntity);
        HotelEntity hotel = hotelRepository.save(hotelEntity);

        return findById(hotel.getId());
    }

    public List<Long> facilityAll(long id) {
        List<Long> byHotelEntityId = hotelFacilityRepository.findByHotelEntity_id(id);
        Collections.sort(byHotelEntityId);
        return byHotelEntityId;
    }


}