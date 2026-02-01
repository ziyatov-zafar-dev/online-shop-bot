package uz.zafar.onlineshoptelegrambot.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;


import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.ms}") long accessExpirationMs,
            @Value("${jwt.refresh.expiration.ms}") long refreshExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(decodeSecret(secret));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }
    public String extractDeviceId(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(ua + "-" + ip);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, User user) {
        String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isExpired(token);
    }

    private String buildToken(User user, long expirationMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(Map.of("role", user.getRole().name()))
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    private byte[] decodeSecret(String secret) {
        try {
            return Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            int len = secret.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(secret.charAt(i), 16) << 4)
                        + Character.digit(secret.charAt(i + 1), 16));
            }
            return data;
        }
    }
}
