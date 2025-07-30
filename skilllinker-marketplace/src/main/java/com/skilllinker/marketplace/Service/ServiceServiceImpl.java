package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.CategoryResponse;
import com.skilllinker.marketplace.DTO.ServiceRequest;
import com.skilllinker.marketplace.DTO.ServiceResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Category;
import com.skilllinker.marketplace.Entity.Service;
import com.skilllinker.marketplace.Repositories.CategoryRepository;
import com.skilllinker.marketplace.Repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceResponse createService(ServiceRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setMinBookingFee(request.getMinBookingFee());
        service.setCategory(category);  // Connect to category (manual sync)
        service = serviceRepository.save(service);


        category.getServices().add(service);
        categoryRepository.save(category);

        return mapToResponse(service);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

        if (request.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));


            if (service.getCategory() != null && !service.getCategory().getId().equals(newCategory.getId())) {
                service.getCategory().getServices().remove(service);
                categoryRepository.save(service.getCategory());
            }

            service.setCategory(newCategory);
            newCategory.getServices().add(service);
            categoryRepository.save(newCategory);
        }

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setMinBookingFee(request.getMinBookingFee());
        service = serviceRepository.save(service);

        return mapToResponse(service);

    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));


        if (service.getCategory() != null) {
            service.getCategory().getServices().remove(service);
            categoryRepository.save(service.getCategory());
        }

        serviceRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }
        serviceRepository.deleteByCategoryId(categoryId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ServiceResponse getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

        return mapToResponse(service);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ServiceResponse> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }
        return serviceRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ServiceResponse> searchByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ServiceResponse> findByMinBookingFeeGreaterThanEqual(BigDecimal minFee) {
        return serviceRepository.findByMinBookingFeeGreaterThanEqual(minFee).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private ServiceResponse mapToResponse(Service service) {
        ServiceResponse response = ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .minBookingFee(service.getMinBookingFee())
                .build();

        if (service.getCategory() != null) {
            Category category = service.getCategory();
            response.setCategory(CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }

        if (service.getBookings() != null) {
            response.setBookings(service.getBookings().stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }


    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .professionalId(booking.getProfessional() != null ? booking.getProfessional().getId() : null)
                .serviceId(booking.getService() != null ? booking.getService().getId() : null)
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .totalFee(booking.getTotalFee())
                .paymentStatus(booking.getPaymentStatus())
                .address(booking.getAddress())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
