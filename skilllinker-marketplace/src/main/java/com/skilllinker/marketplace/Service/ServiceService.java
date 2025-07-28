package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.ServiceRequest;
import com.skilllinker.marketplace.DTO.ServiceResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceService {

    ServiceResponse createService(ServiceRequest request);

    ServiceResponse updateService(Long id, ServiceRequest request);

    void deleteService(Long id);

    void deleteByCategoryId(Long categoryId);

    ServiceResponse getServiceById(Long id);

    List<ServiceResponse> findByCategoryId(Long categoryId);

    List<ServiceResponse> searchByName(String name);

    List<ServiceResponse> findByMinBookingFeeGreaterThanEqual(BigDecimal minFee);
}
