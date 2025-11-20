package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.AdmRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtService;
import biblioteca.onliine.biblioteca.usecase.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// CORREÇÃO: Caminho do pacote corrigido para o subpacote 'controller'
@WebMvcTest(biblioteca.onliine.biblioteca.infrastructure.controller.AdmController.class)
public class AdmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositórios mockados (necessários para o AdmController)
    @MockBean
    private ClienteRepository clienteRepository;
    @MockBean
    private AdmRepository admRepository;
    @MockBean
    private VendaRepository vendaRepository;
    @MockBean
    private LivroRepository livroRepository;

    // Beans de Segurança mockados (necessários para o Spring Security e filtros)
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private CustomUserDetailsService userDetailsService;

    // Mocks de dados
    private Cliente mockCliente;
    private Funcionario mockFuncionario;
    private Livro mockLivroAtivo;
    private Venda mockVenda;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;

    @BeforeEach
    void setUp() {
        // Configura Mocks de Entidades
        mockCliente = new Cliente();
        mockCliente.setId(ID_EXISTENTE);
        mockCliente.setNome("Cliente Teste");

        mockFuncionario = new Funcionario();
        mockFuncionario.setId(ID_EXISTENTE);
        mockFuncionario.setNome("Funcionario Teste");

        mockLivroAtivo = new Livro();
        mockLivroAtivo.setId(ID_EXISTENTE);
        mockLivroAtivo.setTitulo("Livro Teste");
        mockLivroAtivo.setEstadoRegistroLivro(EstadoRegistro.ATIVO);

        mockVenda = new Venda();
        mockVenda.setId(ID_EXISTENTE);
    }

    // ====================================================================
    // TESTES DE CLIENTES
    // ====================================================================

    @Test
    void deveBuscarTodosOsClientesComSucesso() throws Exception {
        // ARRANGE
        List<Cliente> clientes = Arrays.asList(mockCliente, new Cliente());
        when(clienteRepository.findAll()).thenReturn(clientes);

        // ACT & ASSERT: Adicionado autenticação de ADM para evitar 401/403
        mockMvc.perform(get("/adm/cliente")
                        .with(user("admin").roles("ADM"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Cliente Teste"));

        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveDeletarClienteComSucesso() throws Exception {
        // ARRANGE
        when(clienteRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockCliente));
        doNothing().when(clienteRepository).delete(mockCliente);

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF para evitar 401/403
        mockMvc.perform(delete("/adm/cliente/deletar/{id}", ID_EXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf())) // Necessário para requisições DELETE/PUT/POST
                .andExpect(status().isOk())
                .andExpect(content().string("{deleted: 1}"));

        verify(clienteRepository, times(1)).findById(ID_EXISTENTE);
        verify(clienteRepository, times(1)).delete(mockCliente);
    }

    @Test
    void deveRetornarNotFoundAoTentarDeletarClienteInexistente() throws Exception {
        // ARRANGE
        when(clienteRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(delete("/adm/cliente/deletar/{id}", ID_INEXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("{deleted: Resource not found}"));

        verify(clienteRepository, times(1)).findById(ID_INEXISTENTE);
        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    // ====================================================================
    // TESTES DE FUNCIONÁRIOS
    // ====================================================================

    @Test
    void deveBuscarTodosOsFuncionariosComSucesso() throws Exception {
        // ARRANGE
        List<Funcionario> funcionarios = Arrays.asList(mockFuncionario, new Funcionario());
        when(admRepository.findAll()).thenReturn(funcionarios);

        // ACT & ASSERT: Adicionado autenticação de ADM
        mockMvc.perform(get("/adm/buscar-funcionario")
                        .with(user("admin").roles("ADM"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Funcionario Teste"));

        verify(admRepository, times(1)).findAll();
    }

    @Test
    void deveDeletarFuncionarioComSucesso() throws Exception {
        // ARRANGE
        when(admRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockFuncionario));
        doNothing().when(admRepository).delete(mockFuncionario);

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(delete("/adm/deletar/{id}", ID_EXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("{deleted: 1}"));

        verify(admRepository, times(1)).findById(ID_EXISTENTE);
        verify(admRepository, times(1)).delete(mockFuncionario);
    }

    @Test
    void deveRetornarNotFoundAoTentarDeletarFuncionarioInexistente() throws Exception {
        // ARRANGE
        when(admRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(delete("/adm/deletar/{id}", ID_INEXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("{deleted: Resource not found}"));

        verify(admRepository, times(1)).findById(ID_INEXISTENTE);
        verify(admRepository, never()).delete(any(Funcionario.class));
    }

    // ====================================================================
    // TESTES DE VENDAS (Histórico)
    // ====================================================================

    @Test
    void deveBuscarHistoricoDeVendasComSucesso() throws Exception {
        // ARRANGE
        List<Venda> vendas = Collections.singletonList(mockVenda);
        when(vendaRepository.findAll()).thenReturn(vendas);

        // ACT & ASSERT: Adicionado autenticação de ADM
        mockMvc.perform(get("/adm/historico-vendas")
                        .with(user("admin").roles("ADM"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(vendaRepository, times(1)).findAll();
    }

    @Test
    void deveDeletarVendaComSucesso() throws Exception {
        // ARRANGE
        when(vendaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockVenda));
        doNothing().when(vendaRepository).delete(mockVenda);

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(delete("/adm/deletar-historico/{id}", ID_EXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Livro deletado com sucesso"));

        verify(vendaRepository, times(1)).findById(ID_EXISTENTE);
        verify(vendaRepository, times(1)).delete(mockVenda);
    }

    @Test
    void deveRetornarOkAoTentarDeletarVendaInexistente() throws Exception {
        // ARRANGE
        when(vendaRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(delete("/adm/deletar-historico/{id}", ID_INEXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Livro deletado com sucesso"));

        verify(vendaRepository, times(1)).findById(ID_INEXISTENTE);
        verify(vendaRepository, never()).delete(any(Venda.class));
    }

    // ====================================================================
    // TESTES DE LIVROS (Ativar/Desativar)
    // ====================================================================

    @Test
    void deveAtivarLivroComSucesso() throws Exception {
        // ARRANGE
        Livro livroInativo = new Livro();
        livroInativo.setId(ID_EXISTENTE);
        livroInativo.setEstadoRegistroLivro(EstadoRegistro.INATIVO);

        when(livroRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(livroInativo));
        when(livroRepository.save(any(Livro.class))).thenReturn(livroInativo);

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(put("/adm/ativar/{id}", ID_EXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Livro reativado com sucesso!"));

        // VERIFY: Verifica se o status foi alterado para ATIVO antes de salvar
        verify(livroRepository, times(1)).findById(ID_EXISTENTE);
        verify(livroRepository, times(1)).save(argThat(livro -> livro.getEstadoRegistroLivro() == EstadoRegistro.ATIVO));
    }

    @Test
    void deveRetornarNotFoundAoTentarAtivarLivroInexistente() throws Exception {
        // ARRANGE
        when(livroRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(put("/adm/ativar/{id}", ID_INEXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livro não encontrado"));

        verify(livroRepository, times(1)).findById(ID_INEXISTENTE);
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveDesativarLivroComSucesso() throws Exception {
        // ARRANGE
        when(livroRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockLivroAtivo));
        when(livroRepository.save(any(Livro.class))).thenReturn(mockLivroAtivo);

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(put("/adm/desativar/{id}", ID_EXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Livro desativado com sucesso!"));

        // VERIFY: Verifica se o status foi alterado para INATIVO antes de salvar
        verify(livroRepository, times(1)).findById(ID_EXISTENTE);
        verify(livroRepository, times(1)).save(argThat(livro -> livro.getEstadoRegistroLivro() == EstadoRegistro.INATIVO));
    }

    @Test
    void deveRetornarNotFoundAoTentarDesativarLivroInexistente() throws Exception {
        // ARRANGE
        when(livroRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT & ASSERT: Adicionado autenticação de ADM e CSRF
        mockMvc.perform(put("/adm/desativar/{id}", ID_INEXISTENTE)
                        .with(user("admin").roles("ADM"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livro não encontrado"));

        verify(livroRepository, times(1)).findById(ID_INEXISTENTE);
        verify(livroRepository, never()).save(any(Livro.class));
    }
}