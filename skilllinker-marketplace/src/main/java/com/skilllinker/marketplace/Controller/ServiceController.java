package com.skilllinker.marketplace.Controller;

import com.skilllinker.marketplace.DTO.ServiceRequest;
import com.skilllinker.marketplace.DTO.ServiceResponse;
import com.skilllinker.marketplace.Service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> createService(@RequestBody ServiceRequest request) {
        ServiceResponse response = serviceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @RequestBody ServiceRequest request) {
        ServiceResponse response = serviceService.updateService(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByCategoryId(@PathVariable Long categoryId) {
        serviceService.deleteByCategoryId(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        ServiceResponse response = serviceService.getServiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServiceResponse>> findByCategoryId(@PathVariable Long categoryId) {
        List<ServiceResponse> responses = serviceService.findByCategoryId(categoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServiceResponse>> searchByName(@RequestParam String name) {
        List<ServiceResponse> responses = serviceService.searchByName(name);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/min-fee")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServiceResponse>> findByMinBookingFeeGreaterThanEqual(@RequestParam BigDecimal minFee) {
        List<ServiceResponse> responses = serviceService.findByMinBookingFeeGreaterThanEqual(minFee);
        return ResponseEntity.ok(responses);
    }
}
