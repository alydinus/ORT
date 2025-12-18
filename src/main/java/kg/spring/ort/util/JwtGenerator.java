package kg.spring.ort.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kg.spring.ort.entity.Role;
import kg.spring.ort.entity.User;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtGenerator {

    private final UserRepository userRepository;

    private final Key accessSecret;
    private final Key refreshSecret;
    private final long accessExpMs;
    private final long refreshExpMs;

    public JwtGenerator(
            UserRepository userRepository,
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.access-exp-ms}") long accessExpMs,
            @Value("${jwt.refresh-exp-ms}") long refreshExpMs
    ) {
        this.userRepository = userRepository;
        this.accessSecret = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshSecret = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    public static Map readHeaderUnverified(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Bad JWT");
        byte[] headerBytes = Decoders.BASE64URL.decode(parts[0]);
        try {
            return new ObjectMapper().readValue(headerBytes, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse JWT header", e);
        }
    }

    public String generateAccessToken(String username) {
        var now = new Date();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        List<Role> roles = user.getRoles();
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(Role::getName).collect(Collectors.toList())
        );
        return Jwts.builder()
                .setSubject(username)
                .setHeaderParam("typ", "accessToken")
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + accessExpMs))
                .signWith(accessSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        var now = new Date();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        List<Role> roles = user.getRoles();
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(Role::getName).collect(Collectors.toList())
        );
        return Jwts.builder()
                .setSubject(username)
                .setHeaderParam("typ", "refreshToken")
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + refreshExpMs))
                .signWith(refreshSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccess(String token) {
        Map<String, Object> stringObjectMap = readHeaderUnverified(token);
        return "accessToken".equals(stringObjectMap.get("typ"));
    }

    public boolean isRefresh(String token) {
        Map<String, Object> stringObjectMap = readHeaderUnverified(token);
        return "refreshToken".equals(stringObjectMap.get("typ"));
    }

    public String extractUsernameFromAccess(String token) {
        return parseAccess(token).getBody().getSubject();
    }

    public String extractUsernameFromRefresh(String token) {
        return parseRefresh(token).getBody().getSubject();
    }

    public boolean notExpiredAccess(String token) {
        try {
            var exp = parseAccess(token).getBody().getExpiration();
            return exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean notExpiredRefresh(String token) {
        try {
            var exp = parseRefresh(token).getBody().getExpiration();
            return exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parseAccess(String token) {
        return Jwts.parserBuilder().setSigningKey(accessSecret).build().parseClaimsJws(token);
    }

    private Jws<Claims> parseRefresh(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshSecret).build().parseClaimsJws(token);
    }
}
