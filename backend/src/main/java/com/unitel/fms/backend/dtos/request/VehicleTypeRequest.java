package com.unitel.fms.backend.dtos.request;

import com.unitel.fms.backend.contexts.SecurityContextHolder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
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

    public String getName(){
        return switch (SecurityContextHolder.getLang()) {
            case "vi" -> nameVi;
            case "lo" -> nameLo;
            default -> nameEn;
        };
    }
}
