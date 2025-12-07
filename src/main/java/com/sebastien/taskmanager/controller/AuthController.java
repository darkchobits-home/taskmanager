package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.dto.TokenResponseDto;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.security.AuthRequest;
import com.sebastien.taskmanager.security.AuthResponse;
import com.sebastien.taskmanager.security.RefreshTokenRequest;
import com.sebastien.taskmanager.service.AuthService;
import com.sebastien.taskmanager.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserAccountService userAccountService;

    private final UserAccountEntityToDtoConverter userAccountEntityToDtoConverter;

    @Operation(summary = "Login", description = "Log into application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        final AuthResponse authResponse = authService.login(authRequest);

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh", description = "Refresh login token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refresh."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        final TokenResponseDto tokenResponseDto = authService.refresh(refreshTokenRequest);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @Operation(summary = "Me", description = "Return information of the current user logged.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refresh."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/me")
    public ResponseEntity<UserAccountDTO> me(final Authentication authentication) {
        final String username = authentication.getName();
        UserAccount userAccount = userAccountService.getByUsername(username).orElseThrow();

        UserAccountDTO userAccountDTO = userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class);

        return ResponseEntity.ok(userAccountDTO);

    }
    
}
