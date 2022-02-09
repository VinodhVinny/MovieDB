package org.ragnarok.movieDB.service;

import lombok.AllArgsConstructor;
import org.ragnarok.movieDB.dto.AuthRequest;
import org.ragnarok.movieDB.dto.AuthResponse;
import org.ragnarok.movieDB.dto.UserDto;
import org.ragnarok.movieDB.exception.ItemAlreadyExistsException;
import org.ragnarok.movieDB.model.User;
import org.ragnarok.movieDB.repository.UserRepository;
import org.ragnarok.movieDB.util.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public void signup(UserDto userDto) throws ItemAlreadyExistsException {
        Optional<User> u1 = userRepository.findByUsername(userDto.getUsername());
        Optional<User> u2 = userRepository.findByEmailId(userDto.getEmailId());

        if (u1.isPresent()) throw new ItemAlreadyExistsException("User with username: " + userDto.getUsername() + " already exists");
        if (u2.isPresent()) throw new ItemAlreadyExistsException("User with emailId: " + userDto.getEmailId() + " already exists");

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .emailId(userDto.getEmailId())
                .role(userDto.getRole())
                .createdDate(LocalDateTime.now())
                .isEnabled(true)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        String token = jwtTokenUtil.generateJwtToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new AuthResponse(authRequest.getUsername(), token, LocalDateTime.now());
    }
}
