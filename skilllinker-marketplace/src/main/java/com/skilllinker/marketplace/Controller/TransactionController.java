package com.skilllinker.marketplace.Controller;

import com.skilllinker.marketplace.DTO.TransactionRequest;
import com.skilllinker.marketplace.DTO.TransactionResponse;
import com.skilllinker.marketplace.Enums.TransactionStatus;
import com.skilllinker.marketplace.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(@PathVariable Long id, @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransactionStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByStatus(@PathVariable TransactionStatus status) {
        transactionService.deleteByStatus(status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionResponse>> findByStatus(@PathVariable TransactionStatus status) {
        List<TransactionResponse> responses = transactionService.findByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> findByBookingId(@PathVariable Long bookingId) {
        TransactionResponse response = transactionService.findByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
}

