package biblioteca.onliine.biblioteca.infrastructure.seguranca;

public class AccessRoutesUtil {
    public static final String[] ROTAS_LIVRES = {
            // ... (suas rotas livres existentes) ...
            "/auth/login",
            "/auth/cadastro",
            "/auth/cadastrar-funcionario",
            "/auth/cadastro-adm",
            "/venda/**",
            "/livros/ativos",
            "/cliente/**",
            "/livros/{id}",
            "/livros/pdf/{livroId}",
            "/livros/atualizarLivro/{id}",
            "/livros/atualizarLivro/capaPdf/{id}",
            "/livros/capa/{fileName}",
            "/comentarios/adicionarComentario/**",
            "/comentarios/livro/**",
            "/usuarios/**"
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
