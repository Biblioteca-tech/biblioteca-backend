package biblioteca.onliine.biblioteca.infrastructure.controller;


import biblioteca.onliine.biblioteca.domain.dto.LoginDTO;
import biblioteca.onliine.biblioteca.domain.entity.Administrador;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtService;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final FuncionarioRepository funcionarioRepository;
    private final AdmmRepository admRepository;

    public AuthController(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService, FuncionarioRepository funcionarioRepository, AdmmRepository admRepository) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.funcionarioRepository = funcionarioRepository;
        this.admRepository = admRepository;
    }


    // ---------------- Cadastro de cliente ----------------
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastroUsuario(@RequestBody Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        cliente.getRoles().add("ROLE_CLIENTE");
        Cliente clienteSalvo = clienteRepository.save(cliente);
        emailService.enviarEmailCadastro(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok(clienteSalvo);
    }

    // ---------------- Cadastro de funcionário ----------------
    @PostMapping("/cadastrar-funcionario")
    public ResponseEntity<?> cadastroFuncionario(@RequestBody Funcionario funcionario) {
        if (clienteRepository.existsByEmail(funcionario.getEmail())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        funcionario.getRoles().add("ROLE_FUNCIONARIO");
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        return ResponseEntity.ok(funcionarioSalvo);
    }

    // ---------------- cadastro adm ------------------
    @PostMapping("/cadastro-adm")
    public ResponseEntity<?> cadastroAdm(@RequestBody Administrador administrador) {
        administrador.setSenha(passwordEncoder.encode(administrador.getSenha()));
        administrador.getRoles().add("ROLE_ADMINISTRADOR");
        Administrador administrador1 = admRepository.save(administrador);

        return  ResponseEntity.ok(administrador1);
    }

    // ---------------- Login  ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getSenha()));
        var userDetails = (UserDetails) authentication.getPrincipal();


        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        String token = jwtService.generateToken(userDetails.getUsername(), roles);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login efetuado com sucesso!");
        response.put("token", token);
        response.put("roles", roles);

        return ResponseEntity.ok(response);
    }

    
}
