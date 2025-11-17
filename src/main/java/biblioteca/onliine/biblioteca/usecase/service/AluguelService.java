package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import biblioteca.onliine.biblioteca.domain.dto.AluguelDTO;
import biblioteca.onliine.biblioteca.domain.entity.*;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final LivroRepository livroRepository;
    private final ClienteLivroRepository clienteLivroRepository;
    private final TipoAcessoRepository tipoAcessoRepository;



    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          LivroRepository livroRepository, ClienteLivroRepository clienteLivroRepository, TipoAcessoRepository tipoAcessoRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.livroRepository = livroRepository;
        this.tipoAcessoRepository = tipoAcessoRepository;
        this.clienteLivroRepository = clienteLivroRepository;
    }
    public AluguelDTO toDTO(Aluguel aluguel) {
        AluguelDTO dto = new AluguelDTO();
        dto.setId(aluguel.getId());
        dto.setClienteId(aluguel.getCliente().getId());
        dto.setLivroId(aluguel.getLivro().getId());
        dto.setDataAluguel(aluguel.getDataAluguel().toString());
        dto.setDataDevolucao(aluguel.getDataDevolucao() .toString());
        dto.setLivroTitulo(aluguel.getLivro().getTitulo());
        dto.setStatus(aluguel.getStatus()); // ou toString()
        return dto;
    }
    public List<AluguelDTO> toDTOList(List<Aluguel> alugueis) {
        return alugueis.stream().map(this::toDTO).toList();
    }
    public List<AluguelDTO> findAllAtivosDTO() {
        return toDTOList(aluguelRepository.findByStatus(StatusAluguel.ATIVO));
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
        TipoLocacao tl = new TipoLocacao();
        tl.setClienteId(clienteId);
        tl.setLivroId(livroId);
        tl.setTipoAcesso(TipoAcesso.ALUGADO);
        tipoAcessoRepository.save(tl);


        ClienteLivro clienteLivro = new ClienteLivro();
        clienteLivro.setCliente(cliente);
        clienteLivro.setLivro(livro);
        clienteLivro.setTipoAcesso(TipoAcesso.ALUGADO);
        clienteLivro.setDataAdicionado(LocalDateTime.now());
        clienteLivroRepository.save(clienteLivro);

        Aluguel aluguel = Aluguel.builder()
                .cliente(cliente)
                .livro(livro)
                .dataAluguel(LocalDateTime.now())
               .dataDevolucao(LocalDateTime.now().plusMinutes(1))
                .valorAluguel(valorAluguel)
                .status(StatusAluguel.ATIVO)
                .build();


        return aluguelRepository.save(aluguel);
    }


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

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void verificarAlugueisVencidos() {
        List<Aluguel> alugueis = aluguelRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();

        for (Aluguel aluguel : alugueis) {
            if (aluguel.getStatus() == StatusAluguel.ATIVO &&
                    aluguel.getDataDevolucao().isBefore(agora)) {

                aluguel.setStatus(StatusAluguel.FINALIZADO);

                Cliente cliente = aluguel.getCliente();
                Livro livro = aluguel.getLivro();

                if (cliente != null && livro != null) {
                    clienteLivroRepository.deleteByClienteAndLivro(cliente, livro);
                }

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
