package biblioteca.onliine.biblioteca.domain.port.repository;

public interface AtivavelRepository {
    void ativar();
    void desativar();
    boolean isAtivo();
}
