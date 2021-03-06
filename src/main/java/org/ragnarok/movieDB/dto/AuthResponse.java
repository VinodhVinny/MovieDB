package org.ragnarok.movieDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String username;
    private String jwtToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
