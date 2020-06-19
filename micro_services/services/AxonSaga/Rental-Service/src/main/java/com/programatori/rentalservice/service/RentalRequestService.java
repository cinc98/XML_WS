package com.programatori.rentalservice.service;

import com.programatori.rentalservice.dto.ApproveDenyRequestDTO;
import com.programatori.rentalservice.dto.AvailabilityDTO;
import com.programatori.rentalservice.dto.RentalRequestDTO;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

public interface RentalRequestService {

    public ResponseEntity<?> addRentalRequest(List<RentalRequestDTO> rentalRequestDTO) throws ParseException;

    ResponseEntity<?> deleteInvalidRentals(Long vehicleId, AvailabilityDTO availabilityDTO);

    public ResponseEntity<?> approveDenyRequest(ApproveDenyRequestDTO approveDenyRequestDTO);

    public List<?> listPendingRequests(Long ownerId);

    public void clearRequests();

    public ResponseEntity<?> pay(Long requestId);
}
