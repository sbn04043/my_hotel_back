package com.example.hotel_booking.service;


import com.example.hotel_booking.dto.HotelFileDto;
import com.example.hotel_booking.entity.HotelEntity;
import com.example.hotel_booking.entity.HotelFileEntity;
import com.example.hotel_booking.repository.HotelFileRepository;
import com.example.hotel_booking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.MulticastChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelFileService {

    private final HotelRepository hotelRepository;
    private final HotelFileRepository hotelFileRepository;


    @Autowired
    public HotelFileService(HotelFileRepository hotelFileRepository, HotelRepository hotelRepository) {
        this.hotelFileRepository = hotelFileRepository;
        this.hotelRepository = hotelRepository;
    }


    public boolean save(MultipartFile[] files, Long id) throws IOException {
        Optional<HotelEntity> optionalHotelEntity = hotelRepository.findById(id);
        if (optionalHotelEntity.isPresent()) {
            StringBuilder fileNames = new StringBuilder();

            Path uploadPath = Paths.get("src/main/resources/static/hotel");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                String extension = "";

                if (originalFileName != null && originalFileName.contains(".")) {
                    extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
                }

                String storedFileName = System.currentTimeMillis() + "." + extension;
                fileNames.append(",").append(storedFileName);

                Path filePath = uploadPath.resolve(storedFileName);
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                }

                hotelFileRepository.save(HotelFileEntity.builder()
                        .id(id)
                        .originalFileName(originalFileName)
                        .storedFileName(storedFileName)
                        .build());


            }
            return true;
        } else {
            return false;
        }
    }


    public List<HotelFileDto> findByHotelId(long id) {
        List<HotelFileEntity> hotelFileEntityList = hotelFileRepository.findByHotelEntity_id(id);
        List<HotelFileDto> hotelFileDtoList = new ArrayList<>();
        for (HotelFileEntity entity : hotelFileEntityList) {
            hotelFileDtoList.add(HotelFileDto.toHotelFileDto(entity, id));

        }
        return hotelFileDtoList;
    }

    public List<String> findByHotelIdToName(Long id) {
        List<HotelFileEntity> hotelFileEntityList = hotelFileRepository.findByHotelEntity_id(id);
        List<String> hotelFileStoredNameList = new ArrayList<>();
        for (HotelFileEntity hotelFileEntity : hotelFileEntityList) {
            hotelFileStoredNameList.add(HotelFileDto.toHotelFileDto(hotelFileEntity, id).getStoredFileName());
        }
        return hotelFileStoredNameList;
    }
}