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
    ClienteRepository clienteRepository;
    VendaRepository vendaRepository;
    LivroRepository livroRepository;

    public VendaController(ClienteRepository clienteRepository, VendaRepository vendaRepository, LivroRepository livroRepository) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
    }

    @PostMapping("/vender")
    public ResponseEntity<?> simularVenda(@RequestParam Long clienteId, @RequestParam Long livroId) {
        Venda venda = new Venda();
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        Livro livro = livroRepository.findLivroById(livroId);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }
        venda.setCliente(cliente.get());
        venda.setLivro(livro);
        venda.setValor(livro.getPreco());
        venda.setDataVenda(LocalDateTime.now());

        Venda vendaSalva = vendaRepository.save(venda);

        return ResponseEntity.ok().body(vendaSalva);
    }
}
