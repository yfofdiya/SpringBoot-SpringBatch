package com.practice.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private Object data;
    private String message;
}
