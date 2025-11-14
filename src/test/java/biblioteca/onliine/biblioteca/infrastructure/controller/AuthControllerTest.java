package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.LoginDTO;
import biblioteca.onliine.biblioteca.domain.entity.Administrador;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtService;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private AdmmRepository admRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarClienteComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setEmail("teste@email.com");
        cliente.setSenha("123");
        cliente.setNome("Caio");

        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("HASH");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = authController.cadastroUsuario(cliente);

        assertEquals(200, response.getStatusCodeValue());
        Cliente salvo = (Cliente) response.getBody();
        assertNotNull(salvo);
        assertTrue(salvo.getRoles().contains("ROLE_CLIENTE"));
        verify(emailService).enviarEmailCadastro("teste@email.com", "Caio");
    }

    @Test
    void naoDeveCadastrarClienteComEmailExistente() {
        Cliente cliente = new Cliente();
        cliente.setEmail("existe@email.com");

        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.cadastroUsuario(cliente);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Usuário já existe", response.getBody());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void deveCadastrarFuncionarioComSucesso() {
        Funcionario f = new Funcionario();
        f.setEmail("func@email.com");
        f.setSenha("abc");

        when(clienteRepository.existsByEmail(f.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("abc")).thenReturn("HASH");
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = authController.cadastroFuncionario(f);

        assertEquals(200, response.getStatusCodeValue());
        Funcionario salvo = (Funcionario) response.getBody();
        assertTrue(salvo.getRoles().contains("ROLE_FUNCIONARIO"));
    }

    @Test
    void naoDeveCadastrarFuncionarioComEmailExistente() {
        Funcionario f = new Funcionario();
        f.setEmail("duplicado@email.com");

        when(clienteRepository.existsByEmail(f.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.cadastroFuncionario(f);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Usuário já existe", response.getBody());
    }

    @Test
    void deveCadastrarAdministradorComSucesso() {
        Administrador adm = new Administrador();
        adm.setEmail("adm@email.com");
        adm.setSenha("senha");

        when(passwordEncoder.encode("senha")).thenReturn("HASH");
        when(admRepository.save(any(Administrador.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = authController.cadastroAdm(adm);

        assertEquals(200, response.getStatusCodeValue());
        Administrador salvo = (Administrador) response.getBody();
        assertTrue(salvo.getRoles().contains("ROLE_ADMINISTRADOR"));
    }

    @Test
    void deveRealizarLoginComSucesso() {
        LoginDTO login = new LoginDTO();
        login.setEmail("user@email.com");
        login.setSenha("123");

        User user = new User("user@email.com", "HASH", List.of(() -> "ROLE_CLIENTE"));
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(eq("user@email.com"), anySet())).thenReturn("TOKEN123");

        ResponseEntity<?> response = authController.login(login);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Login efetuado com sucesso!", body.get("message"));
        assertEquals("TOKEN123", body.get("token"));
        assertTrue(((Set<?>) body.get("roles")).contains("ROLE_CLIENTE"));
    }

    @Test
    void deveFalharNoLoginComCredenciaisInvalidas() {
        LoginDTO login = new LoginDTO();
        login.setEmail("user@erro.com");
        login.setSenha("errada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authController.login(login);
        });

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(jwtService, never()).generateToken(anyString(), anySet());
    }

    @Test
    void deveFalharNoLoginComErroInterno() {
        LoginDTO login = new LoginDTO();
        login.setEmail("erro@interno.com");
        login.setSenha("qualquer");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Erro interno inesperado"));

        try {
            authController.login(login);
            fail("Deveria lançar uma exceção genérica");
        } catch (RuntimeException e) {
            assertEquals("Erro interno inesperado", e.getMessage());
            verify(jwtService, never()).generateToken(anyString(), anySet());
        }
    }
}
