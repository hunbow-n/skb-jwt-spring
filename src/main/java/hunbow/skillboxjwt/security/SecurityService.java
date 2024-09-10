package hunbow.skillboxjwt.security;

import hunbow.skillboxjwt.entity.RefreshToken;
import hunbow.skillboxjwt.entity.User;
import hunbow.skillboxjwt.exception.RefreshTokenException;
import hunbow.skillboxjwt.repository.UserRepository;
import hunbow.skillboxjwt.security.jwt.JwtUtils;
import hunbow.skillboxjwt.service.RefreshTokenService;
import hunbow.skillboxjwt.web.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse authentificateUser(LoginRequest loginRequest) {

        // из присланных логина и пароля достаем аутификейшн сущность
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // засовываем в контект авторизованного юезера чтобы спринг знал об активном юзере
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // извлекаем информацию об аунтифицированном юзере
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new AuthResponse().builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(roles)
                .email(userDetails.getEmail())
                .token(jwtUtils.generateToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .build();

    }

    public void register(CreateUserRequest createUserRequest) {
        User user = User.builder()
                .email(createUserRequest.getEmail())
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .build();
        user.setRoles(createUserRequest.getRoles());

        userRepository.save(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // мне нужно получив ревфреш токен из реквеста выдать новый аксеесс токен

        String refreshToken = refreshTokenRequest.getRefreshToken();

        return refreshTokenService.findByRefreshToken(refreshToken)
                .map(refreshTokenService::checkRefreshToken) // проверяем не протух ли рефреш токен
                .map(RefreshToken::getUserId) // получаем из него юзер id - он хранится вместе с самим токеном
                .map(userId -> {
                    User tokenOwner = userRepository.findById(userId) // получаем юзера по найденому id
                            .orElseThrow(
                                    () -> new RefreshTokenException("Exceprion trying get token for user " + userId)
                            );
                    // генерим токен по юзернейму найденного юзера
                    String token = jwtUtils.generateTokenFromUsername(tokenOwner.getUsername());
                                                            // создаем нвоый рефреш токен
                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new RefreshTokenException(refreshToken, "Refresh token not found"));

    }

    public void logout() {
        var currentAuthenticatedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(currentAuthenticatedUser instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getId();

            userRepository.deleteById(userId);
        }

    }

}
