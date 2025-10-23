package biblioteca.onliine.biblioteca.infrastructure.seguranca;

public class AccessRoutesUtil {
    public static final String[] ROTAS_LIVRES = {
            "/cliente/login",
            "/livros/listar"
    };
    public static final String[] ROTAS_FUNCIONARIO = {
            "/funcionario/**",
            "/venda/**"
    };

    public static final String[] ROTAS_ADMIN = {
            "/adm/**"
    };
}
