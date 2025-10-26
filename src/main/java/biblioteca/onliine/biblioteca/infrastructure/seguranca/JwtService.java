package biblioteca.onliine.biblioteca.infrastructure.seguranca;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    private static final String SECRET_KEY = "6E58732F7336763979244226452948404D635166546A576E5A72347537782141"; // troca por uma mais complexa

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String generateToken(String email, Set<String> roles) {
        return JWT.create()
                .withSubject(email)
                .withClaim("roles", new ArrayList<>(roles))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // expira em 24h
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return decodedJWT.getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email);
        } catch (Exception e) {
            return false;
        }
    }

}
