package com.unitel.fms.backend.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class VehicleTypeResponse {
    private UUID id;
    private String code;
    private String nameEn;
    private String nameVi;
    private String nameLo;
    private BigDecimal maxLoadTon;
    private Short seatCount;
    private String cargoType;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
