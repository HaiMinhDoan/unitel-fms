package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.Map;

@Data
public class SystemConfigRequest {
    private String configKey;
    private Map<String, Object> configValue;
    private String description;
    private String status;
}
