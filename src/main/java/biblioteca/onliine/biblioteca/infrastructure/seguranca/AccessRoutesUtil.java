package biblioteca.onliine.biblioteca.infrastructure.seguranca;

public class AccessRoutesUtil {
    public static final String[] ROTAS_LIVRES = {
            "/auth/login",
            "/auth/cadastro",
            "/auth/cadastro-adm",
            "/livros/ativos",
            "/livros/{id}",
            "/livros/pdf/{livroId}",
            "/livros/capa/{fileName}",
            "/usuarios/**",
    };
    public static final String[] ROTAS_FUNCIONARIO = {
            "/funcionario/**",
            "/livros/cadastrar",
            "/livros/deletar/{id}",
            "/funcionario/livros",
    };

    public static final String[] ROTAS_ADMIN = {
            "/adm/**",
            "/auth/cadastrar-funcionario",
            "/alugueis/deletar-historico/{id}",
            "/livros/toggle-status/{id}",
            "/livros/atualizarLivro/{id}",
            "/livros/atualizarLivro/capaPdf/{id}",
            "/livros/ativos",
            "/funcionario/livros",
    };

    public static  final String[] ROTAS_CLIENTE = {
            "/cliente/**",
            "/venda/**",
            "/alugueis/alugar",
            "/cliente/meus-livros",
            "/venda/vender",
            "/alugueis/historico-aluguel",

    };
    public static  final String[] ROTAS_COMPARTILHADAS = {
            "/venda/relatorio"
    };
    public static  final String[] ROTAS_COMPARTILHADAS2 = {
            "/livros",
            "/adm/cliente"
    };
}
