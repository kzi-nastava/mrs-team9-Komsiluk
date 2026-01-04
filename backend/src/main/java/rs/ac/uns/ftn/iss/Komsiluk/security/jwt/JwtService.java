package rs.ac.uns.ftn.iss.Komsiluk.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpirationSeconds());

        // Minimalni claims: userId + role
        Map<String, Object> claims = Map.of(
                "role", user.getRole().name()
        );

        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setSubject(String.valueOf(user.getId())) // sub = userId
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long extractUserId(String token) {
        Claims claims = parseAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public UserRole extractRole(String token) {
        Claims claims = parseAllClaims(token);
        String role = claims.get("role", String.class);
        return UserRole.valueOf(role);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(props.getIssuer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
