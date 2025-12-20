package kg.spring.ort.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.spring.ort.service.UserService;
import kg.spring.ort.util.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthentificationFilter extends OncePerRequestFilter {
    private final JwtGenerator jwtGenerator;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            if (jwtGenerator.isAccess(jwt) && jwtGenerator.notExpiredAccess(jwt)) {
                String username = jwtGenerator.extractUsernameFromAccess(jwt);
                authenticate(username);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String username) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        var user = userService.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
