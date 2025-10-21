package biblioteca.onliine.biblioteca.infrastructure.seguranca.auth;

public class AccessRoutesUtil {
    // Rotas públicas
    public static final String[] PUBLIC_ROUTES = {
            "/cliente/cadastro",
            "/cliente/login",
            "/livros/ativos"
    };
    // Rotas de cliente
    public static final String[] CLIENT_ROUTES = {
            "/cliente/**",
            "/venda/**"
    };
    // Rotas de funcionário
    public static final String[] FUNCIONARIO_ROUTES = {
            "/funcionario/**",
            "/livros"
    };
    // Rotas de administrador
    public static final String[] ADMIN_ROUTES = {
            "/adm/**"

    };
}
