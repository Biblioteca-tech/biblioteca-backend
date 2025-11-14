package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.dto.LivroDTO;
import biblioteca.onliine.biblioteca.domain.entity.*;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.usecase.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ConfigUser configUser;
    @Mock
    private EmailService emailService;
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private AluguelService aluguelService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AluguelRepository aluguelRepository;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveTrocarSenhaComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setSenha("123");
        cliente.setEmail("teste@email.com");
        cliente.setNome("Iarley da Silva");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(configUser.loginUsuario(cliente, "123")).thenReturn(true);

        Map<String, String> body = Map.of("senhaAtual", "123", "senhaNova", "novaSenha");

        ResponseEntity<String> response = clienteController.trocarSenha(1L, body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Senha alterada com sucesso.", response.getBody());
        verify(clienteRepository).save(cliente);
        verify(emailService).enviarEmailTrocaSenha(anyString(), anyString());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = clienteController.trocarSenha(1L, Map.of());

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Cliente não encontrado", response.getBody());
    }

    @Test
    void deveRetornarUnauthorizedQuandoSenhaIncorreta() {
        Cliente cliente = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(configUser.loginUsuario(cliente, "errada")).thenReturn(false);

        ResponseEntity<String> response = clienteController.trocarSenha(1L, Map.of(
                "senhaAtual", "errada", "senhaNova", "nova"
        ));

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Senha incorreta", response.getBody());
    }

    @Test
    void deveRetornarLivrosDoCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail("teste@email.com");

        Livro livro1 = new Livro();
        livro1.setTitulo("Livro Comprado");

        Livro livro2 = new Livro();
        livro2.setTitulo("Livro Alugado");

        Venda venda = new Venda();
        venda.setLivro(livro1);

        Aluguel aluguel = new Aluguel();
        aluguel.setLivro(livro2);

        UserDetails user = new User(cliente.getEmail(), "senha", new ArrayList<>());

        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));
        when(vendaRepository.findByClienteId(cliente.getId())).thenReturn(List.of(venda));
        when(aluguelRepository.findByClienteAndStatus(cliente, StatusAluguel.ATIVO))
                .thenReturn(List.of(aluguel));

        ResponseEntity<List<LivroDTO>> response = clienteController.getLivrosDoCliente(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoEncontradoEmGetLivros() {
        UserDetails user = new User("nao@existe.com", "123", new ArrayList<>());
        when(clienteRepository.findByEmail(user.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<List<LivroDTO>> response = clienteController.getLivrosDoCliente(user);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deveAlugarLivroComSucesso() throws Exception {
        Aluguel aluguel = new Aluguel();
        when(aluguelService.alugarLivro(1L, 2L, 7)).thenReturn(aluguel);

        ResponseEntity<?> response = clienteController.alugarLivro(1L, 2L, 7);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(aluguel, response.getBody());
    }

    @Test
    void deveRetornarBadRequestAoFalharAluguel() throws Exception {
        when(aluguelService.alugarLivro(1L, 2L, 7)).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> response = clienteController.alugarLivro(1L, 2L, 7);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Erro", response.getBody());
    }

    @Test
    void deveAtualizarPerfilComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setEmail("teste@email.com");
        UserDetails user = new User(cliente.getEmail(), "senha", new ArrayList<>());

        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Novo Nome");
        body.put("cpf", "12345678900");

        ResponseEntity<?> response = clienteController.atualizarPerfil(user, body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Perfil atualizado com sucesso!", response.getBody());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveRetornarNotFoundAoAtualizarPerfilDeClienteInexistente() {
        UserDetails user = new User("inexistente@email.com", "123", new ArrayList<>());
        when(clienteRepository.findByEmail(user.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = clienteController.atualizarPerfil(user, Map.of());

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Cliente não encontrado", response.getBody());
    }

    @Test
    void deveRetornarErroDeDataInvalidaAoAtualizarPerfil() {
        Cliente cliente = new Cliente();
        cliente.setEmail("teste@email.com");
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        UserDetails user = new User(cliente.getEmail(), "senha", new ArrayList<>());

        Map<String, Object> body = Map.of("data_nascimento", "31-02-2020"); // formato inválido

        ResponseEntity<?> response = clienteController.atualizarPerfil(user, body);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Formato de data inválido. Use yyyy-MM-dd.", response.getBody());
    }

    @Test
    void deveRetornarPerfilComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setEmail("teste@email.com");
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        UserDetails user = new User(cliente.getEmail(), "senha", new ArrayList<>());

        ResponseEntity<?> response = clienteController.getPerfil(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cliente, response.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteEmGetPerfil() {
        UserDetails user = new User("nao@existe.com", "123", new ArrayList<>());
        when(clienteRepository.findByEmail(user.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = clienteController.getPerfil(user);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Cliente não encontrado", response.getBody());
    }
}
