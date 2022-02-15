package org.ragnarok.movieDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("RefreshToken")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken implements Serializable {
    private String id;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
