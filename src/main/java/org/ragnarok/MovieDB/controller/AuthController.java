package org.ragnarok.MovieDB.controller;

import lombok.AllArgsConstructor;
import org.ragnarok.MovieDB.dto.AuthRequest;
import org.ragnarok.MovieDB.dto.AuthResponse;
import org.ragnarok.MovieDB.dto.GenericDto;
import org.ragnarok.MovieDB.dto.UserDto;
import org.ragnarok.MovieDB.exception.ResourceAlreadyExistsException;
import org.ragnarok.MovieDB.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<GenericDto> signup(@RequestBody UserDto userDto) throws ResourceAlreadyExistsException {
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
}
