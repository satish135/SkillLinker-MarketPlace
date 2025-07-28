package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {


    @Modifying
    @Transactional
    @Query("UPDATE Professional p SET p.activeStatus = :active WHERE p.id = :id")
    void updateActiveStatus(Long id, Boolean active);


    @Modifying
    @Transactional
    void deleteByActiveStatus(Boolean active);


    List<Professional> findByCategoriesContaining(String category);


    List<Professional> findBySkillsContaining(String skill);


    List<Professional> findByActiveStatus(Boolean active);


    Optional<Professional> findByUserId(Long userId);
}
