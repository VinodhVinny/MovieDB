package org.ragnarok.MovieDB.service;

import lombok.AllArgsConstructor;
import org.ragnarok.MovieDB.dto.UserDto;
import org.ragnarok.MovieDB.exception.ResourceAlreadyExistsException;
import org.ragnarok.MovieDB.model.User;
import org.ragnarok.MovieDB.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public void signup(UserDto userDto) throws ResourceAlreadyExistsException {

        Optional<User> u1 = userRepository.findByUsername(userDto.getUsername());
        Optional<User> u2 = userRepository.findByEmailId(userDto.getEmailId());

        if (u1.isPresent()) throw new ResourceAlreadyExistsException("User with username: " + userDto.getUsername() + " already exists");
        if (u2.isPresent()) throw new ResourceAlreadyExistsException("User with emailId: " + userDto.getEmailId() + " already exists");

        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .emailId(userDto.getEmailId())
                .role(userDto.getRole())
                .createdDate(LocalDateTime.now())
                .isEnabled(true)
                .build();

        userRepository.save(user);
    }
}
