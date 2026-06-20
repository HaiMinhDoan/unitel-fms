package com.unitel.fms.backend.dtos.request;

import com.unitel.fms.backend.constants.enums.FilterLogicType;
import com.unitel.fms.backend.constants.enums.FilterOperation;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterCriteria {
    String fieldName;
    FilterOperation operation;
    Object value;
    @Builder.Default
    FilterLogicType logicType = FilterLogicType.AND;
}
