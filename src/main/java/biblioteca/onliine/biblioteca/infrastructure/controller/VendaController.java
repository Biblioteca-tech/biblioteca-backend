package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

        // Buscar cliente pelo email
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        Cliente cliente = clienteOpt.get(); // ✅ Obtemos o Cliente do Optional

        // Buscar livro pelo id
        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }

        // Criar venda
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setLivro(livro);
        venda.setValor(livro.getPreco());
        venda.setDataVenda(LocalDateTime.now());

        // Salvar venda
        Venda vendaSalva = vendaRepository.save(venda);

        return ResponseEntity.ok(vendaSalva);
    }
}
