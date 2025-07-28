package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.Config.CustomUserDetails;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.TransactionRequest;
import com.skilllinker.marketplace.DTO.TransactionResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Transaction;
import com.skilllinker.marketplace.Enums.TransactionStatus;
import com.skilllinker.marketplace.Repositories.BookingRepository;
import com.skilllinker.marketplace.Repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public TransactionResponse createTransaction(TransactionRequest request) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getBookingId()));


        if (!isAdmin && !booking.getCustomer().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only create transactions for your own bookings");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setBooking(booking);
        transaction = transactionRepository.save(transaction);


        booking.setTransaction(transaction);
        bookingRepository.save(booking);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionResponse updateTransactionStatus(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));

        transaction.setStatus(request.getStatus());
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));


        if (transaction.getBooking() != null) {
            Booking booking = transaction.getBooking();
            booking.setTransaction(null);
            bookingRepository.save(booking);
        }

        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByStatus(TransactionStatus status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);

        for (Transaction transaction : transactions) {
            if (transaction.getBooking() != null) {
                Booking booking = transaction.getBooking();
                booking.setTransaction(null);
                bookingRepository.save(booking);
            }
        }

        transactionRepository.deleteByStatus(status);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));

        return mapToResponse(transaction);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<TransactionResponse> findByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TransactionResponse findByBookingId(Long bookingId) {
        Transaction transaction = transactionRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for booking ID: " + bookingId));

        return mapToResponse(transaction);
    }


    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = TransactionResponse.builder()
                .id(transaction.getId())
                .bookingId(transaction.getBooking() != null ? transaction.getBooking().getId() : null)
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .paymentMethod(transaction.getPaymentMethod())
                .createdAt(transaction.getCreatedAt())
                .build();


        if (transaction.getBooking() != null) {
            Booking booking = transaction.getBooking();
            response.setBooking(BookingResponse.builder()
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
                    .build());
        }

        return response;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
