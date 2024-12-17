package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.ParkingSpace;
import com.moliveira.demo_park_api.exception.CodeUniqueValidationException;
import com.moliveira.demo_park_api.exception.EntityNotFoundException;
import com.moliveira.demo_park_api.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;

    @Transactional
    public ParkingSpace save(ParkingSpace parkingSpace) {
        try {
            return parkingSpaceRepository.save(parkingSpace);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueValidationException(String.format(
                    "Parking Space with code '%s' already exists",
                    parkingSpace.getCode()
            ));
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpace findByCode(String code) {
        return parkingSpaceRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Parking Space with code '%s' not found", code))
        );
    }
}
