package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final LivroRepository livroRepository;

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          LivroRepository livroRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.livroRepository = livroRepository;
    }

    // Cria um novo aluguel
    public Aluguel alugarLivro(Long clienteId, Long livroId, int dias) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }

        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            throw new RuntimeException("Livro não encontrado");
        }

        double valorAluguel = livro.getPreco() * 0.2 * dias; // 20% do preço por dia

        Aluguel aluguel = Aluguel.builder()
                .cliente(clienteOpt.get())
                .livro(livro)
                .dataAluguel(LocalDateTime.now())
                .dataDevolucao(LocalDateTime.now().plusDays(dias))
                .valorAluguel(valorAluguel)
                .status(StatusAluguel.ATIVO)
                .build();

        return aluguelRepository.save(aluguel);
    }

    // Marca um aluguel como devolvido
    public Aluguel devolverLivro(Long aluguelId) {
        Aluguel aluguel = aluguelRepository.findById(aluguelId)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado"));

        aluguel.setStatus(StatusAluguel.DEVOLVIDO);
        aluguel.setDataDevolucao(LocalDateTime.now());

        return aluguelRepository.save(aluguel);
    }

    // Busca todos os alugueis ativos
    public List<Aluguel> findAllAtivos() {
        return aluguelRepository.findByStatus(StatusAluguel.ATIVO);
    }

    // Busca todos os alugueis de um cliente específico
    public List<Aluguel> findByCliente(Cliente cliente) {
        return aluguelRepository.findByCliente(cliente);
    }

    // Busca alugueis de um cliente com um status específico
    public List<Aluguel> findByClienteAndStatus(Cliente cliente, StatusAluguel status) {
        return aluguelRepository.findByCliente(cliente).stream()
                .filter(a -> a.getStatus() == status)
                .toList();
    }

    // Salva ou atualiza um aluguel
    public Aluguel save(Aluguel aluguel) {
        // Garante que o status nunca será null
        if (aluguel.getStatus() == null) {
            aluguel.setStatus(StatusAluguel.ATIVO);
        }
        return aluguelRepository.save(aluguel);
    }
}
