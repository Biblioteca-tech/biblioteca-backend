package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/venda")
public class VendaController {

    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;

    public VendaController(ClienteRepository clienteRepository, VendaRepository vendaRepository, LivroRepository livroRepository) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
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

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setLivro(livro);
        venda.setValor(livro.getPreco());
        venda.setDataVenda(LocalDateTime.now());

        // Salvar venda
        Venda vendaSalva = vendaRepository.save(venda);

        return ResponseEntity.ok(vendaSalva);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<?> getRelatorio() {
        var vendas = vendaRepository.findAll();

        var relatorio = vendas.stream().map(v -> {
            var cliente = v.getCliente();
            var livro = v.getLivro();

            return Map.of(
                    "clienteEmail", cliente != null ? cliente.getEmail() : "N/A",
                    "livroTitulo", livro != null ? livro.getTitulo() : "N/A",
                    "livroGenero", livro != null ? livro.getGenero() : "N/A",
                    "preco", livro != null ? livro.getPreco() : 0.0,
                    "dataVenda", v.getDataVenda()
            );
        }).toList();

        return ResponseEntity.ok(relatorio);
    }

}
