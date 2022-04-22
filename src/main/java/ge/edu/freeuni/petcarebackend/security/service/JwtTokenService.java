package ge.edu.freeuni.petcarebackend.security.service;

import ge.edu.freeuni.petcarebackend.security.RandomStringGenerator;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService {

    @Value("${access.token.expiration.minutes}")
    private Long accessTokenExpiration;

    @Value("${refresh.token.expiration.minutes}")
    private Long refreshTokenExpiration;

    @PostConstruct
    private void init() {
        if (accessTokenExpiration > refreshTokenExpiration) {
            System.exit(0);
        }
    }

    private final String SECRET_KEY = new RandomStringGenerator(30, new SecureRandom()).nextString();

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public AuthorizationTokensDTO generateTokens(UserEntity user) {
        return new AuthorizationTokensDTO(generateAccessToken(user), generateRefreshToken(user));
    }

    private String generateAccessToken(UserEntity user) {
        return createToken(
                new HashMap<>() {{
                    put("full_name", "%s %s".formatted(user.getFirstname(), user.getLastname()));
                    put("azp", "petcare");
                    put("typ", "Bearer");
                }},
                user.getUsername(),
                accessTokenExpiration
        );
    }

    private String generateRefreshToken(UserEntity user) {
        return createToken(
                new HashMap<>() {{
                    put("azp", "petcare");
                    put("typ", "Refresh");
                }},
                user.getUsername(),
                refreshTokenExpiration
        );
    }

    private String createToken(Map<String, Object> claims, String subject, Long duration) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * duration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isRefreshToken(String refreshToken) {
        return extractAllClaims(refreshToken).get("typ", String.class).equals("Refresh");
    }
}
