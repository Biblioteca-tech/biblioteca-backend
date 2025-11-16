package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.*;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteLivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final LivroRepository livroRepository;
    private final ClienteLivroRepository clienteLivroRepository;

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          LivroRepository livroRepository, ClienteLivroRepository clienteLivroRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.livroRepository = livroRepository;
        this.clienteLivroRepository = clienteLivroRepository;
    }

    public Aluguel alugarLivro(Long clienteId, Long livroId, int dias) {

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }
        Cliente cliente = clienteOpt.get();

        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            throw new RuntimeException("Livro não encontrado");
        }

        double valorAluguel = livro.getPreco();

        ClienteLivro clienteLivro = new ClienteLivro();
        clienteLivro.setCliente(cliente);
        clienteLivro.setLivro(livro);
        clienteLivroRepository.save(clienteLivro);

        Aluguel aluguel = Aluguel.builder()
                .cliente(cliente)
                .livro(livro)
                .dataAluguel(LocalDateTime.now())
                .dataDevolucao(LocalDateTime.now().plusDays(dias))
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


    public Aluguel save(Aluguel aluguel) {
        if (aluguel.getStatus() == null) {
            aluguel.setStatus(StatusAluguel.ATIVO);
        }
        return aluguelRepository.save(aluguel);
    }

    public List<Aluguel> listarHistorico() {
        return aluguelRepository.findAll();
    }

    @Scheduled(fixedRate = 60000)
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
    public Map<String, Object> gerarRelatorioAluguel() {

        List<Aluguel> alugueis = aluguelRepository.findAll();

        long quantidade = alugueis.size();
        double totalRecebido = alugueis.stream()
                .mapToDouble(Aluguel::getValorAluguel)
                .sum();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("quantidade", quantidade);
        relatorio.put("totalRecebido", totalRecebido);
        relatorio.put("alugueis", alugueis);

        return relatorio;
    }
}
