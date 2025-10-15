package biblioteca.onliine.biblioteca.domain.port.repository;

public interface Ativavel {
    void ativar();
    void desativar();
    boolean isAtivo();
}
