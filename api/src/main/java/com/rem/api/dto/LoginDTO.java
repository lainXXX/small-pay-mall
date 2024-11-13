package com.rem.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
    private String ticket;
    private String loginKey;
}
