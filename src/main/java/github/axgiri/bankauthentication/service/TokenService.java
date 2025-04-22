package github.axgiri.bankauthentication.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import github.axgiri.bankauthentication.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey  publicKey;

    private static final long ACCESS_TOKEN_TTL_MIN = 10;

    public String extractUsername(String jwt) {
        try {
            return extractAllClaimsAsync(jwt).join().getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("Cannot extract username: " + e.getMessage());
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<Claims> extractAllClaimsAsync(String jwt) {
        log.info("extracting claims from token: {}", jwt);
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)  // проверяем по публичному ключу
                .build()
                .parseClaimsJws(jwt)
                .getBody();
            return CompletableFuture.completedFuture(claims);
        } catch (Exception e) {
            log.error("Invalid JWT: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> generateTokenAsync(UserDetails userDetails) {
        Instant now = Instant.now();
        Map<String, Object> extra = new HashMap<>();
        extra.put("roles", userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .collect(Collectors.toList()));

        String token = Jwts.builder()
            .setClaims(extra)
            .setSubject(userDetails.getUsername())
            .setId(UUID.randomUUID().toString())  // jti — для отзыва
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(ACCESS_TOKEN_TTL_MIN, ChronoUnit.MINUTES)))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
        return CompletableFuture.completedFuture(token);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        try {
            Claims c = extractAllClaimsAsync(jwt).join();
            return c.getSubject().equals(userDetails.getUsername())
                && c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
