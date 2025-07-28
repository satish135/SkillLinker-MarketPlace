package com.skilllinker.marketplace.Controller;

import com.skilllinker.marketplace.DTO.ProfessionalProfileRequest;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;
import com.skilllinker.marketplace.Service.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<ProfessionalProfileResponse> createProfessional(@RequestBody ProfessionalProfileRequest request) {
        ProfessionalProfileResponse response = professionalService.createProfessional(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<ProfessionalProfileResponse> updateProfessional(@PathVariable Long id, @RequestBody ProfessionalProfileRequest request) {
        ProfessionalProfileResponse response = professionalService.updateProfessional(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<Void> updateActiveStatus(@PathVariable Long id, @RequestParam Boolean active) {
        professionalService.updateActiveStatus(id, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfessional(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfessionalProfileResponse> getProfessionalById(@PathVariable Long id) {
        ProfessionalProfileResponse response = professionalService.getProfessionalById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/categories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessionalProfileResponse>> searchByCategories(@RequestParam String category) {
        List<ProfessionalProfileResponse> responses = professionalService.searchByCategories(category);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessionalProfileResponse>> searchBySkills(@RequestParam String skill) {
        List<ProfessionalProfileResponse> responses = professionalService.searchBySkills(skill);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessionalProfileResponse>> findByActiveStatus(@RequestParam Boolean active) {
        List<ProfessionalProfileResponse> responses = professionalService.findByActiveStatus(active);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfessionalProfileResponse> findByUserId(@PathVariable Long userId) {
        ProfessionalProfileResponse response = professionalService.findByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
