package biblioteca.onliine.biblioteca.infrastructure.seguranca.auth;

public class SecurityConstants {
    public static final String SECRET = "chave_super_secreta_leley_2025"; // muda pra uma chave forte
    public static final long EXPIRATION_TIME = 864_000_000; // 10 dias
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
