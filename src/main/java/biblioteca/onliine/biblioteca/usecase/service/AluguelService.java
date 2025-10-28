package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AluguelService {
    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final LivroRepository livroRepository;

    public AluguelService(AluguelRepository aluguelRepository, ClienteRepository clienteRepository, LivroRepository livroRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.livroRepository = livroRepository;
    }

    public Aluguel alugarLivro(Long clienteId, Long livroId, int dias) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }
        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            throw new RuntimeException("Livro não encontrado");
        }
        double valorAluguel = livro.getPreco() * 0.2 * dias; // Exemplo: 20% do valor do livro por dia

        Aluguel aluguel = Aluguel.builder()
                .cliente(cliente.get())
                .livro(livro)
                .dataAluguel(LocalDateTime.now())
                .dataDevolucao(LocalDateTime.now().plusDays(dias))
                .valorAluguel(valorAluguel)
                .status(StatusAluguel.ATIVO)
                .build();

        return aluguelRepository.save(aluguel);
    }
    public Aluguel devolverLivro(Long aluguelId) {
        Aluguel aluguel = aluguelRepository.findById(aluguelId)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado"));

        aluguel.setStatus(StatusAluguel.DEVOLVIDO);
        aluguel.setDataDevolucao(LocalDateTime.now());

        return aluguelRepository.save(aluguel);
    }
}
