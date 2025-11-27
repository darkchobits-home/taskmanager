package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.dto.TokenResponseDto;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import com.sebastien.taskmanager.security.AuthRequest;
import com.sebastien.taskmanager.security.AuthResponse;
import com.sebastien.taskmanager.security.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(final AuthRequest authRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Object details = authentication.getDetails();

        final String token = jwtService.generateAccessToken(userDetails);
        final String refreshToken = addRefreshTokenToUser(userDetails);


        return new AuthResponse(token, refreshToken);
    }

    private String addRefreshTokenToUser(final UserDetails userDetails) {
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        UserAccountModel userAccountModel = userAccountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userAccountModel.setRefreshToken(refreshToken);
        userAccountRepository.save(userAccountModel);

        return refreshToken;
    }

    public TokenResponseDto refresh(RefreshTokenRequest refreshTokenRequest) {
        UserAccountModel userAccountModel = userAccountRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken()).orElseThrow(() -> {
            UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
            userAccountException.getDetails().put("refreshToken", refreshTokenRequest.getRefreshToken());

            return userAccountException;
        });

        if (!jwtService.isRefreshTokenValid(refreshTokenRequest.getRefreshToken(), userAccountModel)) {
            throw new RuntimeException("Refresh token expired or invalid.");
        }

        UserDetails userDetails = new User(userAccountModel.getUsername(),
                userAccountModel.getPassword(),
                userAccountModel.getRoles().stream()
                        .map(roleModel -> new SimpleGrantedAuthority("ROLE_" + roleModel.getName().name()))
                        .collect(Collectors.toList()));

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return new TokenResponseDto(newAccessToken);
    }
}
