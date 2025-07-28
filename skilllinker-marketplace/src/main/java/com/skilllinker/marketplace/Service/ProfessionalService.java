package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.ProfessionalProfileRequest;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;

import java.util.List;

public interface ProfessionalService {

    ProfessionalProfileResponse createProfessional(ProfessionalProfileRequest request);

    ProfessionalProfileResponse updateProfessional(Long id, ProfessionalProfileRequest request);

    void updateActiveStatus(Long id, Boolean active);

    void deleteProfessional(Long id);

    ProfessionalProfileResponse getProfessionalById(Long id);

    List<ProfessionalProfileResponse> searchByCategories(String category);

    List<ProfessionalProfileResponse> searchBySkills(String skill);

    List<ProfessionalProfileResponse> findByActiveStatus(Boolean active);

    ProfessionalProfileResponse findByUserId(Long userId);
}
