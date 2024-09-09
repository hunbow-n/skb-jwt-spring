package hunbow.skillboxjwt.security.jwt;


import hunbow.skillboxjwt.security.AppUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${spring.app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public String generateToken(AppUserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername());
    }

    // кодируем имя пользователя через сет сабджект с помощью сикрет в jwt
    private String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.ES512, jwtSecret)
                .compact();
    }

    // вытаскиваем из jwt токена с помощью секрета наш сабджект - у нас это юзернейм, чтобы потом сравнить
    // с именем, которое передаст пользотель
    public String getUsername(String token) {
        return parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean validate(String authToken) {
        try {
           parseClaimsJwt(authToken);
           return true;
        } catch (SignatureException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
        };
        return false;
    }

    private Jws<Claims> parseClaimsJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token);
    }

}
