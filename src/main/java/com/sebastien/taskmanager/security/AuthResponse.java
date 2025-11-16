package com.sebastien.taskmanager.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private Long expiresInMillis;

}
