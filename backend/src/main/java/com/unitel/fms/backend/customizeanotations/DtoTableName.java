package com.unitel.fms.backend.customizeanotations;

public @interface DtoTableName {
    String[] tables() default {};
}
