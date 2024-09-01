package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.entity.ReservationEntity;
import com.example.hotel_booking.entity.RoomEntity;
import com.example.hotel_booking.entity.RoomTypeEntity;
import com.example.hotel_booking.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRepository reservationRepository;

    public RoomDto insert(RoomDto roomDto) {
        return RoomDto.toRoomDto(roomRepository.save(RoomEntity.toInsertEntity(roomDto)));
    }

    @Transactional
    public List<RoomDto> selectAll(Long hotelId) {
        HotelEntity hotelEntity = hotelRepository.findById(hotelId).get();
        List<RoomEntity> roomEntityList = roomRepository.findAllByHotelEntityOrderByIdDesc(hotelEntity);
        List<RoomDto> roomDtoList = new ArrayList<>();
        for (RoomEntity roomEntity : roomEntityList) {
            RoomDto roomDto = RoomDto.toRoomDto(roomEntity);
            roomDtoList.add(roomDto);
        }
        return roomDtoList;
    }

    @Transactional
    public RoomDto selectOne(Long roomId) {
        Optional<RoomEntity> optionalRoomEntity = roomRepository.findById(roomId);
//        System.out.println(optionalRoomEntity);
        if (optionalRoomEntity.isPresent()) {
            RoomEntity roomEntity = optionalRoomEntity.get();
            RoomDto roomDto = RoomDto.toRoomDto(roomEntity);
            return roomDto;
        } else {
            return null;
        }
    }

    @Transactional
    public RoomDto update(RoomDto roomDto) {
        HotelEntity hotelEntity = hotelRepository.findById(roomDto.getHotelId()).get();
        RoomTypeEntity roomTypeEntity = roomTypeRepository.findById(roomDto.getRoomTypeId()).get();
        RoomEntity roomEntity = RoomEntity.toUpdateEntity(roomDto, hotelEntity, roomTypeEntity);
        roomRepository.save(roomEntity);
        return selectOne(roomDto.getId());
    }

    @Transactional
    public Boolean delete(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public List<RoomDto> selectAllByCondition(String startDate, String endDate, Long hotelId, Integer peopleCount) {
        List<RoomDto> roomDtoList = new ArrayList<>();
        List<RoomEntity> roomEntityList = roomRepository.findAllByHotelId(hotelId);

        for (int i = 0; i < roomEntityList.size(); i++) {
            RoomEntity roomEntity = roomEntityList.get(i);
            System.out.println(roomEntity.toString());

            List<ReservationEntity> reservationEntityList = reservationRepository.findAllByRoomId(roomEntity.getId());
            int num = 0;
            System.out.println(startDate + " " + endDate);
            for (ReservationEntity reservationEntity : reservationEntityList) {
                String tempStartDate = reservationEntity.getStartDate().toString();
                String tempEndDate = reservationEntity.getEndDate().toString();

                String r_startDate = tempStartDate.substring(0, 4) + tempStartDate.substring(5, 7) + tempStartDate.substring(8, 10);
                String r_endDate = tempEndDate.substring(0, 4) + tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10);

                System.out.println(r_startDate + " " + r_endDate);

                int start = Integer.parseInt(startDate);
                int end = Integer.parseInt(endDate);
                int r_start = Integer.parseInt(r_startDate);
                int r_end = Integer.parseInt(r_endDate);

                if (start > r_start && start < r_end
                        || end > r_start && end < r_end
                        || r_start > start && r_start < end
                        || r_end > start && r_end < end
                        || start < r_start && end > r_end
                        || r_start < start && r_end > end
                        || r_start == start && r_end == end
                ) {
                    num++;
                    System.out.println(num);
                }
            }

            RoomEntity tempRoomEntity = (RoomEntity) roomEntity.clone();
            tempRoomEntity.setRoomMax(roomEntity.getRoomMax() - num);
            roomDtoList.add(RoomDto.toRoomDto(tempRoomEntity));
        }

        return roomDtoList;
    }
}
