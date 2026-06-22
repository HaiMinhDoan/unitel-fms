package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VehicleTypeRequest {
    private String code;
    private String nameEn;
    private String nameVi;
    private String nameLo;
    private BigDecimal maxLoadTon;
    private Short seatCount;
    private String cargoType;
    private String status;
}
