package org.ragnarok.MovieDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1,
            initialValue = 1000
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "user_sequence"
    )
    private Long id;

    @Column(
            name = "username",
            unique = true,
            nullable = false
    )
    private String username;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(
            name = "email_id",
            unique = true,
            nullable = false
    )
    private String emailId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDateTime createdDate;

    private Boolean isEnabled;
}
