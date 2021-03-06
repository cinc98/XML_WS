package com.programatori.rentalservice.controller;

import com.programatori.rentalservice.client.UsersClient;
import com.programatori.rentalservice.client.VehicleClient;
import com.programatori.rentalservice.dto.*;
import com.programatori.rentalservice.models.RentalRequest;
import com.programatori.rentalservice.models.Vehicle;
import com.programatori.rentalservice.service.RentalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RentalController {

    @Autowired
    RentalRequestService rentalRequestService;

    @Autowired
    VehicleClient vehicleClient;

    @Autowired
    UsersClient usersClient;

    @GetMapping("/hello")
    public ResponseEntity<?> get() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return new ResponseEntity<>(String.format("Hello from Rental service with ip address %s!", ip), HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRenatalRequests(@RequestBody List<RentalRequestDTO> rentalRequestDTOS) throws  ParseException {
        System.out.println(rentalRequestDTOS);
        for (RentalRequestDTO dto: rentalRequestDTOS) {
            System.out.println(dto.getVehicleId() + " "+ dto.getFromDate()+ " "+ dto.getToDate());
        }
        Object pricnicpal  = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println((String) pricnicpal);
        return rentalRequestService.addRentalRequest(rentalRequestDTOS);
    }

    @DeleteMapping(path = "/{vehicleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkValidationOfRenatalRequests(@PathVariable Long vehicleId, @RequestBody AvailabilityDTO availabilityDTO) {

        return rentalRequestService.deleteInvalidRentals(vehicleId, availabilityDTO);
    }

    @GetMapping(path = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomersRenatalRequests(HttpServletRequest request, @PathVariable Long customerId){

        String token = request.getHeader("Authorization");
        List<UserDTO> users = usersClient.getUsers();
        List<VehicleBasicDTO> vehicleBasicDTOS = vehicleClient.getVehicles(token);
        List<RentalRequest> rentalRequests = rentalRequestService.getCustomersRentalRequests(customerId);
        List<RentalRequestResponseDTO> rentalRequestResponseDTOS = new ArrayList<>();

        for (RentalRequest r : rentalRequests) {
            RentalRequestResponseDTO rentalRequestResponseDTO = new RentalRequestResponseDTO();

            for (UserDTO u: users) {
                if(u.getId() == r.getCustomerId()){
                    rentalRequestResponseDTO.setIssuer(u);
                }else if(u.getId() == r.getOwnerId()){
                    rentalRequestResponseDTO.setOwner(u);
                }
            }

            for (VehicleBasicDTO v: vehicleBasicDTOS) {
                for (Vehicle vehicle :r.getVehicleIds()) {
                    if(vehicle.getId() == v.getId()){
                        rentalRequestResponseDTO.setVehicleBasicDTO(v);
                    }
                }
            }
            rentalRequestResponseDTO.setId(r.getId());
            rentalRequestResponseDTO.setFromDate(r.getFromDate());
            rentalRequestResponseDTO.setToDate(r.getToDate());
            rentalRequestResponseDTO.setStatus(r.getStatus().toString());

            rentalRequestResponseDTOS.add(rentalRequestResponseDTO);
        }

        return new ResponseEntity<>(rentalRequestResponseDTOS,HttpStatus.OK);
    }

    @GetMapping(path = "/owner/{ownerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOwnersRenatalRequests(HttpServletRequest request, @PathVariable Long ownerId){

        String token = request.getHeader("Authorization");
        List<UserDTO> users = usersClient.getUsers();
        List<VehicleBasicDTO> vehicleBasicDTOS = vehicleClient.getVehicles(token);
        List<RentalRequest> rentalRequests = rentalRequestService.getOwnersRentalRequests(ownerId);
        List<RentalRequestResponseDTO> rentalRequestResponseDTOS = new ArrayList<>();

        for (RentalRequest r : rentalRequests) {
            RentalRequestResponseDTO rentalRequestResponseDTO = new RentalRequestResponseDTO();

            for (UserDTO u: users) {
                if(u.getId() == r.getCustomerId()){
                    rentalRequestResponseDTO.setIssuer(u);
                }else if(u.getId() == r.getOwnerId()){
                    rentalRequestResponseDTO.setOwner(u);
                }
            }

            for (VehicleBasicDTO v: vehicleBasicDTOS) {
                for (Vehicle vehicle :r.getVehicleIds()) {
                    if(vehicle.getId() == v.getId()){
                        rentalRequestResponseDTO.setVehicleBasicDTO(v);
                    }
                }
            }
            rentalRequestResponseDTO.setId(r.getId());
            rentalRequestResponseDTO.setFromDate(r.getFromDate());
            rentalRequestResponseDTO.setToDate(r.getToDate());
            rentalRequestResponseDTO.setStatus(r.getStatus().toString());

            rentalRequestResponseDTOS.add(rentalRequestResponseDTO);
        }

        return new ResponseEntity<>(rentalRequestResponseDTOS,HttpStatus.OK);
    }

    @GetMapping("/pending/{owner}")
    public ResponseEntity<?> getPendingRequestsByOwner(@PathVariable Long owner){
        return new ResponseEntity<>(rentalRequestService.listPendingRequests(owner),HttpStatus.OK);
    }

    @GetMapping(path = "/{id}", produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRentalRequestById(@PathVariable Long id){
        return rentalRequestService.getById(id);
    }

    @GetMapping(path = "/{customerId}/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean getRentalRequestByParams(@PathVariable Long customerId,@PathVariable Long vehicleId) throws ParseException {

        return rentalRequestService.getRentalRequestByParams(customerId,vehicleId);
    }

    @PostMapping(path = "/approval", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveDenyRentalRequest(@RequestBody ApproveDenyRequestDTO approveDenyRequestDTO){
        return rentalRequestService.approveDenyRequest(approveDenyRequestDTO);
    }

    @Scheduled(fixedRate=10000)
    public void clearRequests() {
        rentalRequestService.clearRequests();
    }

    @PostMapping(path="/{id}/pay")
    public ResponseEntity<?> payRental(@PathVariable Long id){
        return rentalRequestService.pay(id);
    }

    @GetMapping(path= "/pending-requests/{owner}")
    public ResponseEntity<?> test(HttpServletRequest request, @PathVariable Long owner){
        String token = request.getHeader("Authorization");
        List<UserDTO> users = usersClient.getUsers();
        List<VehicleBasicDTO> vehicleBasicDTOS = vehicleClient.getVehicles(token);
        List<RentalRequest> rentalRequests = (List<RentalRequest>) rentalRequestService.listPendingRequests(owner);
        List<RentalRequestResponseDTO> rentalRequestResponseDTOS = new ArrayList<>();
        for (RentalRequest r : rentalRequests) {
            System.out.println(r.getCreationTime());
            RentalRequestResponseDTO rentalRequestResponseDTO = new RentalRequestResponseDTO();
            UserDTO userDTO = null;
            for (UserDTO u: users) {
                System.out.println(u.getId());
                if(u.getId() == r.getCustomerId())
                    userDTO = u;
            }
            rentalRequestResponseDTO.setIssuer(userDTO);
            for (UserDTO u: users) {
                System.out.println(u.getId());

                if(u.getId() == r.getOwnerId())
                    userDTO = u;
            }
            VehicleBasicDTO vehicleBasicDTO = null;
            for (VehicleBasicDTO v: vehicleBasicDTOS) {
                System.out.println(v.getId());
                for (Vehicle vehicle :r.getVehicleIds()) {
                    if(vehicle.getId() == v.getId())
                        vehicleBasicDTO = v;
                }
            }
            rentalRequestResponseDTO.setId(r.getId());
            rentalRequestResponseDTO.setOwner(userDTO);
            rentalRequestResponseDTO.setFromDate(r.getFromDate());
            rentalRequestResponseDTO.setToDate(r.getToDate());
            rentalRequestResponseDTO.setVehicleBasicDTO(vehicleBasicDTO);
            rentalRequestResponseDTOS.add(rentalRequestResponseDTO);
        }
        return new ResponseEntity<>(rentalRequestResponseDTOS,HttpStatus.OK);
    }

    @GetMapping(path= "/reserved-requests/{customer}")
    public ResponseEntity<?> reservedRequests(HttpServletRequest request, @PathVariable Long customer){
        String token = request.getHeader("Authorization");
        List<UserDTO> users = usersClient.getUsers();
        List<VehicleBasicDTO> vehicleBasicDTOS = vehicleClient.getVehicles(token);
        List<RentalRequest> rentalRequests = (List<RentalRequest>) rentalRequestService.listReservedRequests(customer);
        List<RentalRequestResponseDTO> rentalRequestResponseDTOS = new ArrayList<>();
        for (RentalRequest r : rentalRequests) {
            System.out.println(r.getCreationTime());
            RentalRequestResponseDTO rentalRequestResponseDTO = new RentalRequestResponseDTO();
            UserDTO userDTO = null;
            for (UserDTO u: users) {
                System.out.println(u.getId());
                if(u.getId() == r.getCustomerId())
                    userDTO = u;
            }
            rentalRequestResponseDTO.setIssuer(userDTO);
            for (UserDTO u: users) {
                System.out.println(u.getId());

                if(u.getId() == r.getOwnerId())
                    userDTO = u;
            }
            VehicleBasicDTO vehicleBasicDTO = null;
            for (VehicleBasicDTO v: vehicleBasicDTOS) {
                System.out.println(v.getId());
                for (Vehicle vehicle :r.getVehicleIds()) {
                    if(vehicle.getId() == v.getId())
                        vehicleBasicDTO = v;
                }
            }
            rentalRequestResponseDTO.setId(r.getId());
            rentalRequestResponseDTO.setOwner(userDTO);
            rentalRequestResponseDTO.setFromDate(r.getFromDate());
            rentalRequestResponseDTO.setToDate(r.getToDate());
            rentalRequestResponseDTO.setVehicleBasicDTO(vehicleBasicDTO);
            rentalRequestResponseDTOS.add(rentalRequestResponseDTO);
        }
        return new ResponseEntity<>(rentalRequestResponseDTOS,HttpStatus.OK);
    }

}
