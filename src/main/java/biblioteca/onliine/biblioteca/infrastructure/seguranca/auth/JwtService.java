package biblioteca.onliine.biblioteca.infrastructure.seguranca.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("CLIENTE"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public static Key getSigningKey() {
        // Converte a String secreta em uma chave que o JJWT entende
        return Keys.hmacShaKeyFor(SecurityConstants.SECRET.getBytes());
    }

    public String extractEmail(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.SECRET);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    public String extractRole(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.SECRET);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaim("role").asString();
    }
    // 🔹 Extrai o username (sub) do token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 Extrai qualquer claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // 🔹 Valida o token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
