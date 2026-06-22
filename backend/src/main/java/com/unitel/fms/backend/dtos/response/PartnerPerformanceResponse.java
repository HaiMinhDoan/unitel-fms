package com.unitel.fms.backend.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerPerformanceResponse {
    private UUID id;
    private UUID partnerId;
    private String periodMonth;
    private Integer totalTrips;
    private Integer onTimeTrips;
    private BigDecimal avgCost;
    private BigDecimal totalRevenue;
    private String notes;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
