package com.skilllinker.marketplace.DTO;

import com.skilllinker.marketplace.Enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private BookingStatus status;
}
