package biblioteca.onliine.biblioteca.infrastructure.seguranca;

public class AccessRoutesUtil {
    public static final String[] ROTAS_LIVRES = {
            "/auth/login",
            "/auth/cadastro",
            "/auth/cadastrar-funcionario",
            "/venda/**",
            "/livros/ativos",
            "/cliente/**",
            "livros/{id}"
    };
    public static final String[] ROTAS_FUNCIONARIO = {
            "/funcionario/**",
            "/venda/**",
            "/livros/cadastrar",
            "/livros/deletar/{id}"
    };

    public static final String[] ROTAS_ADMIN = {
            "/adm/**"
    };
}
