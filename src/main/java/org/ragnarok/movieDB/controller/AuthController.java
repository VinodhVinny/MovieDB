package org.ragnarok.movieDB.controller;

import lombok.AllArgsConstructor;
import org.ragnarok.movieDB.dto.*;
import org.ragnarok.movieDB.exception.InvalidTokenException;
import org.ragnarok.movieDB.exception.ItemAlreadyExistsException;
import org.ragnarok.movieDB.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<GenericDto> signup(@RequestBody UserDto userDto) throws ItemAlreadyExistsException {
        authService.signup(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GenericDto("User signup successful", HttpStatus.CREATED, LocalDateTime.now()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponse);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) throws InvalidTokenException {
        AuthResponse authResponse = authService.refreshToken(refreshTokenDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponse);
    }

    @PostMapping("/logoutHandler")
    public ResponseEntity<GenericDto> logout() throws InvalidTokenException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GenericDto.builder()
                        .message("User Logged out")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @GetMapping("/test")
    public String test() {
        return "hello friend!";
    }
}
