package hunbow.skillboxjwt.security.jwt;

import hunbow.skillboxjwt.security.UserDetailsServiceImpl;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = generateToken(request);

            if(jwtToken != null && jwtUtils.validate(jwtToken)) {
                String username = jwtUtils.getUsername(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception ex) {
            log.error("Cannot set user authification {} " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /*
        В процессе аутентификации Spring Security:

        Принимает ваши учетные данные (например, пользователь и пароль).
        Проверяет их с помощью AuthenticationManager.
        После успешной аутентификации создается объект UsernamePasswordAuthenticationToken, который содержит информацию о пользователе и его ролях.

        Этот объект потом хранится в SecurityContextHolder и используется для проверки прав доступа при каждом запросе.
        Он фактически заменяет реальный токен или пароль в памяти приложения для упрощения проверки прав пользователя.
    *
    * */


    private String generateToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
