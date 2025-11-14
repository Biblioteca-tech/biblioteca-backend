package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

    public Aluguel alugarLivro(Long clienteId, Long livroId, int dias) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }
        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            throw new RuntimeException("Livro não encontrado");
        }
        double valorAluguel = livro.getPreco();

        Aluguel aluguel = Aluguel.builder()
                .cliente(clienteOpt.get())
                .livro(livro)
                .dataAluguel(LocalDateTime.now())
//                .dataDevolucao(LocalDateTime.now().plusDays(10))
                .dataDevolucao(LocalDateTime.now().plusSeconds(5))
                .valorAluguel(valorAluguel)
                .status(StatusAluguel.ATIVO)
                .build();

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


    // Salva ou atualiza um aluguel
    public Aluguel save(Aluguel aluguel) {
        if (aluguel.getStatus() == null) {
            aluguel.setStatus(StatusAluguel.ATIVO);
        }
        return aluguelRepository.save(aluguel);
    }

    public List<Aluguel> listarHistorico() {
        return aluguelRepository.findAll();
    }

    @Scheduled(fixedRate = 5000)
    public void verificarAlugueisVencidos() {
        List<Aluguel> alugueis = aluguelRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();

        for (Aluguel aluguel : alugueis) {
            if (aluguel.getStatus() == StatusAluguel.ATIVO &&
                    aluguel.getDataDevolucao().isBefore(agora)) {

                aluguel.setStatus(StatusAluguel.FINALIZADO);
                aluguelRepository.save(aluguel);
            }
        }
    }
}
