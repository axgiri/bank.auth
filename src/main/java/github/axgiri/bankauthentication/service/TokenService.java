package github.axgiri.bankauthentication.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import github.axgiri.bankauthentication.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String KEY;
        
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("failed to extract username from token", e);
            throw new InvalidTokenException("failed to extract username from token: " + token + "\n" + e.getMessage());
        }
    }
    
    @Async("asyncExecutor")
    public CompletableFuture<Claims> extractAllClaims(String token) {
        log.info("extracting claims from token: {}", token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return CompletableFuture.completedFuture(claims);
        } catch (Exception e) {
            log.error("token validation failed: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final CompletableFuture<Claims> claim = extractAllClaims(token);
        return claim.thenApply(claimsResolver).join();
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> generateTokenAsync(Map<String, Object> extraClaims, UserDetails userDetails){
        log.info("generating token for user: {}", userDetails.getUsername());
        extraClaims.put("roles", userDetails.getAuthorities().stream()
        .map(authority -> authority.getAuthority().replace("ROLE_", ""))
        .collect(Collectors.toList()));
        try {
            log.debug("generating future token for user with phone number: {}", userDetails.getUsername());
            String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
            return CompletableFuture.completedFuture(token);
        } catch (Exception e) {
            log.info("failed to generate token for user: {}", userDetails.getUsername(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> generateToken(UserDetails userDetails){
        return generateTokenAsync(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
