package com.unitel.fms.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchEligibilityResult {
    private boolean vehicleDocsOk;
    private boolean healthOk;
    private boolean eligible;
}
