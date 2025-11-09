package biblioteca.onliine.biblioteca.infrastructure.seguranca;

public class AccessRoutesUtil {
    public static final String[] ROTAS_LIVRES = {
            // ... (suas rotas livres existentes) ...
            "/auth/login",
            "/auth/cadastro",
            "/auth/cadastro-adm",
            "/venda/**",
            "/livros/ativos",
            "/cliente/**",
            "/livros/{id}",
            "/alugueis/alugar",
            "/livros/pdf/{livroId}",
            "/livros/capa/{fileName}",
            "/usuarios/**",
    };
    public static final String[] ROTAS_FUNCIONARIO = {
            "/funcionario/**",
            "/venda/**",
            "/livros/cadastrar",
            "/livros/deletar/{id}",
            "/funcionario/livros",
    };

    public static final String[] ROTAS_ADMIN = {
            "/adm/**",
            "/auth/cadastrar-funcionario",
            "/alugueis/historico-aluguel",
            "/alugueis/deletar-historico/{id}",
            "/livros/toggle-status/{id}",
            "/livros/atualizarLivro/{id}",
            "/livros/atualizarLivro/capaPdf/{id}",
            "/livros/ativos",
            "/livros",
            "/funcionario/livros",
    };
}
