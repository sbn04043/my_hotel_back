package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.ReservationDto;
import com.example.hotel_booking.entity.ReservationEntity;
import com.example.hotel_booking.entity.*;
import com.example.hotel_booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final RoomFileRepository roomFileRepository;

    public Long insert(ReservationDto reservationDto) {
        Optional<RoomEntity> optionalRoomEntity = roomRepository.findById(reservationDto.getRoomId());
        Optional<UserEntity> optionalUserEntity = userRepository.findById(reservationDto.getUserId());
        if (optionalRoomEntity.isPresent()) {
            RoomEntity roomEntity = optionalRoomEntity.get();
            UserEntity userEntity = optionalUserEntity.get();
            ReservationEntity reservationEntity = ReservationEntity.toInsertEntity(reservationDto,userEntity , roomEntity);
            return reservationRepository.save(reservationEntity).getId();
        }
        return null;
    }

    @Transactional
    public List<ReservationDto> selectAll(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).get();
        List<ReservationEntity> reservationEntityList = reservationRepository.findAllByGuestEntity(userEntity);
        List<ReservationDto> reservationDtoList = new ArrayList<>();
        for (ReservationEntity reservationEntity : reservationEntityList) {
            ReservationDto reservationDto = ReservationDto.toReservationDto(reservationEntity,reservationEntity.getGuestEntity(),reservationEntity.getRoomEntity());
            reservationDtoList.add(reservationDto);
        }

        return  reservationDtoList;
    }

    public Map<?, ?> selectOne(Long roomId) {
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("reservationDto", reservationRepository.findById(roomId).get());
        resultMap.put("roomDto", roomRepository.findById(roomId).get());
        resultMap.put("roomTypeList", roomTypeRepository.findAll());
        resultMap.put("roomFileDtoList", roomFileRepository.findByRoomEntity_id(roomId));

        return resultMap;
    }

    // 예약 취소
    @Transactional
    public ReservationDto cancled(ReservationDto reservationDto) {
        reservationDto.setEnabled(0);

        UserEntity userEntity = userRepository.findById(reservationDto.getUserId()).get();
        RoomEntity roomEntity = roomRepository.findById(reservationDto.getRoomId()).get();
        ReservationEntity reservationEntity = ReservationEntity.toInsertEntity(reservationDto, userEntity, roomEntity);

        reservationRepository.save(reservationEntity);


        return reservationDto;
    }

    public List<ReservationDto> findAllByGuestId(Long id) {
        List<ReservationEntity> reservationEntities = reservationRepository.findByGuestId(id);
        List<ReservationDto> reservationDtoList = new ArrayList<>();
        for (ReservationEntity reservationEntity : reservationEntities) {
            reservationDtoList.add(ReservationDto.toReservationDto(reservationEntity));
        }
        return reservationDtoList;
    }

    public ReservationDto save(ReservationEntity reservationEntity) {
        return ReservationDto.toReservationDto(reservationRepository.save(reservationEntity));
    }
}
