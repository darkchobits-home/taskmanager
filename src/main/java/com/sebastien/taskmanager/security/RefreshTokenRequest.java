package com.sebastien.taskmanager.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshTokenRequest {

    private String refreshToken;
}
