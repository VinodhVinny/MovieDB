package org.ragnarok.movieDB.controller;

import lombok.AllArgsConstructor;
import org.ragnarok.movieDB.dto.AuthRequest;
import org.ragnarok.movieDB.dto.AuthResponse;
import org.ragnarok.movieDB.dto.GenericDto;
import org.ragnarok.movieDB.dto.UserDto;
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
}
