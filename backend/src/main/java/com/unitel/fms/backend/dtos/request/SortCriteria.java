package com.unitel.fms.backend.dtos.request;

import com.unitel.fms.backend.constants.enums.SortDirection;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SortCriteria {
    String fieldName;
    SortDirection direction;
}
