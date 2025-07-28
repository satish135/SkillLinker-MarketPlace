package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.TransactionRequest;
import com.skilllinker.marketplace.DTO.TransactionResponse;
import com.skilllinker.marketplace.Enums.TransactionStatus;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse updateTransactionStatus(Long id, TransactionRequest request);

    void deleteTransaction(Long id);

    void deleteByStatus(TransactionStatus status);

    TransactionResponse getTransactionById(Long id);

    List<TransactionResponse> findByStatus(TransactionStatus status);

    TransactionResponse findByBookingId(Long bookingId);
}
