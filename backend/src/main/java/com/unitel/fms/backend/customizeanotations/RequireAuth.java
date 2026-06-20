package com.unitel.fms.backend.customizeanotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    String[] roles() default {}; // Các role được phép
    boolean inWorkspace() default false;

    LogicType rolesLogic() default LogicType.OR;

    enum LogicType {
        AND, OR
    }
}
