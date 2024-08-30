package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.entity.ReservationEntity;
import com.example.hotel_booking.entity.RoomEntity;
import com.example.hotel_booking.entity.RoomTypeEntity;
import com.example.hotel_booking.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class RoomService {


    private final HotelRepository HOTEL_REPOSITORY;
    private final RoomRepository ROOM_REPOSITORY;
    private final RoomFileRepository ROOM_FILE_REPOSITORY;
    private final RoomTypeRepository ROOM_TYPE_REPOSITORY;
    private final ReservationRepository RESERVATION_REPOSITORY;

    @Autowired
    public RoomService(HotelRepository hotelRepository,
                       RoomRepository roomRepository,
                       RoomTypeRepository roomTypeRepository,
                       RoomFileRepository roomFileRepository,
                       ReservationRepository reservationRepository) {
        this.HOTEL_REPOSITORY = hotelRepository;
        this.ROOM_REPOSITORY = roomRepository;
        this.ROOM_FILE_REPOSITORY = roomFileRepository;
        this.ROOM_TYPE_REPOSITORY = roomTypeRepository;
        this.RESERVATION_REPOSITORY = reservationRepository;
    }

    public Long insert(RoomDto roomDto) throws IOException {

        Optional<HotelEntity> optionalHotelEntity = HOTEL_REPOSITORY.findById(roomDto.getHotelId());
        if (optionalHotelEntity.isPresent()) {
            HotelEntity hotelEntity = optionalHotelEntity.get();

            RoomTypeEntity roomTypeEntity = ROOM_TYPE_REPOSITORY.findById(roomDto.getRoomTypeId()).get();
            RoomEntity roomEntity = RoomEntity.toInsertEntity(roomDto, hotelEntity, roomTypeEntity);
            return ROOM_REPOSITORY.save(roomEntity).getId();

        }
        return null;
    }

    @Transactional
    public List<RoomDto> selectAll(Long hotelId) {
        HotelEntity hotelEntity = HOTEL_REPOSITORY.findById(hotelId).get();
        List<RoomEntity> roomEntityList = ROOM_REPOSITORY.findAllByHotelEntityOrderByIdDesc(hotelEntity);
        List<RoomDto> roomDtoList = new ArrayList<>();
        for (RoomEntity roomEntity : roomEntityList) {
            RoomDto roomDto = RoomDto.toRoomDto(roomEntity, hotelId);
            roomDtoList.add(roomDto);
        }
        return roomDtoList;
    }

    @Transactional
    public RoomDto selectOne(Long roomId) {
        Optional<RoomEntity> optionalRoomEntity = ROOM_REPOSITORY.findById(roomId);
//        System.out.println(optionalRoomEntity);
        if (optionalRoomEntity.isPresent()) {
            RoomEntity roomEntity = optionalRoomEntity.get();
            RoomDto roomDto = RoomDto.toRoomDto(roomEntity, roomEntity.getHotelEntity().getId());
            return roomDto;
        } else {
            return null;
        }
    }

    @Transactional
    public RoomDto update(RoomDto roomDto) {
        HotelEntity hotelEntity = HOTEL_REPOSITORY.findById(roomDto.getHotelId()).get();
        RoomTypeEntity roomTypeEntity = ROOM_TYPE_REPOSITORY.findById(roomDto.getRoomTypeId()).get();
        RoomEntity roomEntity = RoomEntity.toUpdateEntity(roomDto, hotelEntity, roomTypeEntity);
        ROOM_REPOSITORY.save(roomEntity);
        return selectOne(roomDto.getId());
    }

    @Transactional
    public void delete(Long id) {
        ROOM_REPOSITORY.deleteById(id);
    }

    @Transactional
    public List<RoomDto> selectAllByCondition(String startDate, String endDate, Long hotelId, Integer peopleCount) {
        List<RoomDto> roomDtoList = new ArrayList<>();
        List<RoomEntity> roomEntityList = ROOM_REPOSITORY.findAllByHotelId(hotelId);

        for (int i = 0; i < roomEntityList.size(); i++) {
            RoomEntity roomEntity = roomEntityList.get(i);
            System.out.println(roomEntity.toString());

            List<ReservationEntity> reservationEntityList = RESERVATION_REPOSITORY.findAllByRoomId(roomEntity.getId());
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
            roomDtoList.add(RoomDto.toRoomDto(tempRoomEntity, hotelId));
        }

        return roomDtoList;
    }
}
