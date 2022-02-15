package org.ragnarok.movieDB.service;

import lombok.AllArgsConstructor;
import org.ragnarok.movieDB.dto.*;
import org.ragnarok.movieDB.exception.InvalidTokenException;
import org.ragnarok.movieDB.exception.ItemAlreadyExistsException;
import org.ragnarok.movieDB.model.RefreshToken;
import org.ragnarok.movieDB.model.User;
import org.ragnarok.movieDB.repository.RefreshTokenRepository;
import org.ragnarok.movieDB.repository.UserRepository;
import org.ragnarok.movieDB.util.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    @Transactional
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

    @Transactional
    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        String jwtToken = jwtTokenUtil.generateJwtToken((UserDetails) authentication.getPrincipal());
        String refreshToken = jwtTokenUtil.generateRefreshToken(authRequest.getUsername());

        RefreshToken refreshTokenObj = RefreshToken.builder()
                .id(authRequest.getUsername())
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getRefreshTokenExpirationTime()/1000))
                .build();

        refreshTokenRepository.save(refreshTokenObj);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthResponse.builder()
                .username(authRequest.getUsername())
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getJwtExpirationTime()/1000))
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenDto refreshTokenDto) throws InvalidTokenException {
        if (!jwtTokenUtil.isValidToken(refreshTokenDto.getRefreshToken())) {
            throw new InvalidTokenException("Provided refresh token is invalid. May be token expired or invalid token");
        }

        String username = jwtTokenUtil.getUserNameFromToken(refreshTokenDto.getRefreshToken());
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(username);
        if (optionalRefreshToken.isEmpty() || !optionalRefreshToken.get().getRefreshToken().equals(refreshTokenDto.getRefreshToken())) {
            throw new InvalidTokenException("Provided refresh token is invalid. May be token expired or invalid token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String jwtToken = jwtTokenUtil.generateJwtToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(username);

        RefreshToken refreshTokenObj = RefreshToken.builder()
                .id(username)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getRefreshTokenExpirationTime()/1000))
                .build();

        refreshTokenRepository.save(refreshTokenObj);

        return AuthResponse.builder()
                .username(username)
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getJwtExpirationTime()/1000))
                .build();
    }
}
