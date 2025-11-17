package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteLivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/venda")
public class VendaController {

    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;
    private final ClienteLivroRepository clienteLivroRepository;

    public VendaController(ClienteRepository clienteRepository, VendaRepository vendaRepository, LivroRepository livroRepository,  ClienteLivroRepository clienteLivroRepository) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
        this.clienteLivroRepository = clienteLivroRepository;
    }

    @PostMapping("/vender")
    public ResponseEntity<?> simularVenda(@RequestParam String email, @RequestParam Long livroId) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        Cliente cliente = clienteOpt.get();

        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }

        ClienteLivro clienteLivro = new ClienteLivro();
        clienteLivro.setCliente(cliente);
        clienteLivro.setTipoAcesso(TipoAcesso.COMPRADO);
        clienteLivro.setLivro(livro);
        clienteLivroRepository.save(clienteLivro);

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setLivro(livro);
        venda.setValor(livro.getPreco());
        venda.setDataVenda(LocalDateTime.now());
        //venda.setDataVenda(LocalDateTime.of(2025, 9, 10, 10, 0));
        Venda vendaSalva = vendaRepository.save(venda);

        return ResponseEntity.ok(vendaSalva);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<?> getRelatorio() {
        var vendas = vendaRepository.findAll();

        var relatorio = vendas.stream().map(v -> {
            var cliente = v.getCliente();
            var livro = v.getLivro();

            Integer idade = null;
            if (cliente != null && cliente.getData_nascimento() != null) {
                idade = Period.between(cliente.getData_nascimento(), LocalDate.now()).getYears();
            }

            return Map.of(
                    "clienteEmail", cliente != null ? cliente.getEmail() : "N/A",
                    "clienteGenero", cliente != null ? cliente.getGenero() : "N/A",
                    "clienteIdade", idade != null ? idade : 0,
                    "livroTitulo", livro != null ? livro.getTitulo() : "N/A",
                    "livroGenero", livro != null ? livro.getGenero() : "N/A",
                    "preco", livro != null ? livro.getPreco() : 0.0,
                    "dataVenda", v.getDataVenda()
            );
        }).toList();

        return ResponseEntity.ok(relatorio);
    }

}
