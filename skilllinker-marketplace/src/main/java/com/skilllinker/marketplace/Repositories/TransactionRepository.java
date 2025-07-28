package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.Transaction;
import com.skilllinker.marketplace.Enums.TransactionStatus;  // Assume this enum exists
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :id")
    void updateStatus(Long id, TransactionStatus status);


    @Modifying
    @Transactional
    void deleteByStatus(TransactionStatus status);


    List<Transaction> findByStatus(TransactionStatus status);


    Optional<Transaction> findByBookingId(Long bookingId);
}
