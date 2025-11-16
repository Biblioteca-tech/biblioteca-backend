package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteLivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteLivroService {

    private final ClienteLivroRepository clienteLivroRepository;
    private final ClienteRepository clienteRepository;

    public ClienteLivroService(
            ClienteLivroRepository clienteLivroRepository,
            ClienteRepository clienteRepository
    ) {
        this.clienteLivroRepository = clienteLivroRepository;
        this.clienteRepository = clienteRepository;
    }


    public void removerLivroDaBiblioteca(Long livroId) {
        Long clienteId = clienteLogado().getId();

        if (!clienteLivroRepository.existsByClienteIdAndLivroId(clienteId, livroId)) {
            throw new RuntimeException("Este livro não está na sua biblioteca.");
        }

        if (clienteLivroRepository.existsByClienteIdAndLivroId(clienteId, livroId)) {
            clienteLivroRepository.deleteByClienteIdAndLivroId(clienteId, livroId);
        }
    }

    public List<ClienteLivro> listarMeusLivros() {
        Long clienteId = clienteLogado().getId();
        return clienteLivroRepository.findByClienteId(clienteId);
    }

    private Cliente clienteLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

}
