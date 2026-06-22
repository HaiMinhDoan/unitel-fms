package com.unitel.fms.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerPerformanceRequest {
    @NotNull(message = "partner_id is required")
    private UUID partnerId;

    @NotBlank(message = "period_month is required")
    @Size(min = 7, max = 7, message = "period_month must be YYYY-MM")
    private String periodMonth;

    @NotNull(message = "total_trips is required")
    private Integer totalTrips;

    @NotNull(message = "on_time_trips is required")
    private Integer onTimeTrips;

    private BigDecimal avgCost;

    private BigDecimal totalRevenue;

    private String notes;

    @Size(max = 50)
    private String status;
}
