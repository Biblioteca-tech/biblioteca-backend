package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.AdminUpdateDTO;
import biblioteca.onliine.biblioteca.domain.dto.FuncionarioInputDTO;
import biblioteca.onliine.biblioteca.domain.dto.GerenciarClientesDTO;
import biblioteca.onliine.biblioteca.domain.entity.*;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.usecase.service.AdmService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/adm")
public class AdmController {

    @Value("${spring.diretorio.iarley}")
    private String diretorio;

    private final ClienteRepository clienteRepository;
    private final AdmRepository admRepository;
    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdmService admService;


    public AdmController(AdmService admService  ,ClienteRepository clienteRepository, AdmRepository admRepository, VendaRepository vendaRepository, LivroRepository livroRepository,  FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.admRepository = admRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.admService = admService;
    }

    // BUSCAR TODOS OS CLIENTES //
    @GetMapping("/cliente")
    public List<GerenciarClientesDTO> buscarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(l -> new GerenciarClientesDTO(
                        l.getId(),
                        l.getNome(),
                        l.getEmail(),
                        l.getData_nascimento(),
                        l.getCpf(),
                        l.getEstadoRegistroCliente()
                )).toList();
    }


    @DeleteMapping("/cliente/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            clienteRepository.delete(clienteOptional.get());
            return "{deleted: " + id + "}";
        } else {
            return "{deleted: Resource not found}";
        }
    }

    // BUSCAR OS FUNCIONARIOS REGISTRADOS //
    @GetMapping("/buscar-funcionario")
    public List<Funcionario> buscarFuncionarios() {
        return admRepository.findAll();
    }

    @GetMapping("/funcionario/{id}")
    public ResponseEntity<Funcionario> buscarFuncionario(@PathVariable Long id) {
        return admRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETAR FUNCIONARIO
    @DeleteMapping("/deletar/{id}")
    public String deletarFuncionario(@PathVariable Long id) {
        Optional<Funcionario> funcionarioOptional = admRepository.findById(id);
        if (funcionarioOptional.isPresent()) {
            admRepository.delete(funcionarioOptional.get());
            return "{deleted: " + id + "}";
        }
        return "{deleted: Resource not found}";
    }

    // EXIBIR HISTORICO DE LIVROS VENDIDOS //
    @GetMapping("/historico-vendas")
    public List<Venda> buscarVendas() {
        return vendaRepository.findAll();
    }

    // DELETAR HISTORICO LIVRO //
    @DeleteMapping("/deletar-historico/{id}")
    public ResponseEntity<?> deletarVenda(@PathVariable Long id) {
        Optional<Venda> vendaOptional = vendaRepository.findById(id);
        vendaOptional.ifPresent(vendaRepository::delete);
        return ResponseEntity.ok().body("Livro deletado com sucesso");
    }

    // ATIVAR LIVRO //
    @PutMapping(value = "/ativar/{id}")
    public ResponseEntity<String> ativarLivro(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.ativar();
                    livroRepository.save(livro);
                    return ResponseEntity.ok("Livro reativado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado"));
    }

    // DESATIVAR LIVRO //
    @PutMapping(value = "/desativar/{id}")
    public ResponseEntity<String> desativarLivro(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.desativar();
                    livroRepository.save(livro);
                    return ResponseEntity.ok("Livro desativado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado"));
    }

    @PutMapping("/atualizarDados/{id}")
    public ResponseEntity<String> atualizarDadosFuncionario(@PathVariable Long id, @RequestBody FuncionarioInputDTO dadosAtualizados) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(id);

        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado.");
        }

        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(dadosAtualizados.getSenha());
            dadosAtualizados.setSenha(senhaCriptografada);
        }

        Funcionario funcionarioExistente = funcionarioOpt.get();
        funcionarioExistente.atualizarDados(dadosAtualizados);
        funcionarioRepository.save(funcionarioExistente);

        return ResponseEntity.ok("Dados do funcionário atualizados com sucesso!");
    }
    @GetMapping("/perfil")
    public Administrador getPerfil() {
        return admService.getPerfilLogado();
    }

    @PutMapping("/perfil")
    public Administrador atualizarPerfil(@RequestBody AdminUpdateDTO dto) {
        return admService.atualizarPerfil(dto);
    }

    @GetMapping(value = "/pdf/{livroId}")
    public ResponseEntity<Resource> abrirPdf(@PathVariable Long livroId) throws MalformedURLException {
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isEmpty()) return ResponseEntity.notFound().build();
        Livro livro = livroOpt.get();

        File file = new File(this.diretorio + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


}
