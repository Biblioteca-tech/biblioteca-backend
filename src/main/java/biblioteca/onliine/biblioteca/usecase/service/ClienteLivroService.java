package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.dto.ClienteLivroDTO;
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


    public void removerLivroDaBiblioteca(Long clienteLivroId) {
        ClienteLivro item = clienteLivroRepository.findById(clienteLivroId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado."));

        clienteLivroRepository.delete(item);
    }

    public List<ClienteLivroDTO> listarMeusLivros() {
        Long clienteId = clienteLogado().getId();

        List<ClienteLivro> lista = clienteLivroRepository.findByClienteId(clienteId);

        return lista.stream()
                .map(this::toDTO)
                .toList();
    }


    private Cliente clienteLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
    private ClienteLivroDTO toDTO(ClienteLivro clienteLivro) {
        ClienteLivroDTO dto = new ClienteLivroDTO();

        dto.setId(clienteLivro.getId());
        dto.setLivroId(clienteLivro.getLivro().getId());
        dto.setTitulo(clienteLivro.getLivro().getTitulo());
        dto.setAutor(clienteLivro.getLivro().getAutor());
        dto.setStatus(clienteLivro.getLivro().getEstadoRegistroLivro().name());
        dto.setCapaPath(clienteLivro.getLivro().getCapaPath());
        dto.setPdfPath(clienteLivro.getLivro().getPdfPath());
        dto.setDataAdicionado(clienteLivro.getDataAdicionado().toString());

        return dto;
    }

}
