package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {


    @Modifying
    @Transactional
    void deleteByCategoryId(Long categoryId);


    List<Service> findByCategoryId(Long categoryId);


    List<Service> findByNameContainingIgnoreCase(String name);


    @Query("SELECT s FROM Service s WHERE s.minBookingFee >= :minFee")
    List<Service> findByMinBookingFeeGreaterThanEqual(BigDecimal minFee);
}
