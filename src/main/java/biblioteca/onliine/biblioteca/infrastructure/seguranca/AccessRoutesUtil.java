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
    };
    public static final String[] ROTAS_FUNCIONARIO = {
            "/funcionario/**",
            "/livros/cadastrar",
            "/funcionario/livros",
    };

    public static final String[] ROTAS_ADMIN = {
            "/adm/**",
            "/auth/cadastrar-funcionario",
            "/alugueis/deletar-historico/{id}",
            "/livros/atualizarLivro/capaPdf/{id}",
            "/livros/ativos",
            "/funcionario/livros",
            "/adm/atualizarDados/{id}"
    };

    public static  final String[] ROTAS_CLIENTE = {
            "/cliente/**",
            "/venda/**",
            "/alugueis/alugar",
            "/cliente/meus-livros",
            "/venda/vender",
            "/alugueis/historico-aluguel",
            "/meus/remover/{livroId}",
            "/meus"

    };
    public static  final String[] ROTAS_COMPARTILHADAS = {
            "/venda/relatorio",
    };
    public static  final String[] ROTAS_COMPARTILHADAS2 = {
            "/livros",
            "/adm/cliente",
            "/livros/toggle-status/{id}",
            "/usuarios/**",
            "/livros/atualizarLivro/{id}",
            "/livros/deletar/{id}",
            "/livros/{id}/capa",
            "/livros/status",
            "/alugueis/relatorio-aluguel",
            "/venda/relatorio-aluguel"
    };
}
