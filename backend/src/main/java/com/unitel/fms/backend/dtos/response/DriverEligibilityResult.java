package com.unitel.fms.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverEligibilityResult {
    boolean eligible;
    String reason;
}
