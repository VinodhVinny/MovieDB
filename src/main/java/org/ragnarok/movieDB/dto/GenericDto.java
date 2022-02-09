package org.ragnarok.movieDB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericDto {
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
