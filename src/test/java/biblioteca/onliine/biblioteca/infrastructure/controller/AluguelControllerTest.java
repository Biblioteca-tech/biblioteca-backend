package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente; // Novo Import
import biblioteca.onliine.biblioteca.domain.entity.Livro;   // Novo Import
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtAuthenticationFilter;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Configuração para carregar apenas a "fatia" Web (AluguelController),
// excluindo a segurança explícita e auto-configurada que causa a falha de contexto.
@WebMvcTest(
        controllers = AluguelController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
public class AluguelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Dependências do Controller (devem ser Mockadas)
    @MockBean
    private AluguelService aluguelService;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private LivroService livroService;

    // Moca a dependência AluguelRepository.
    @MockBean
    private AluguelRepository aluguelRepository;

    // Moca o filtro JWT para evitar a falha de inicialização do ApplicationContext
    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    private MockAluguelRequest requestBody;
    private Aluguel aluguelRetorno;

    // ENDPOINT: POST /alugueis
    private final String BASE_URL = "/alugueis";

    // --- CLASSES MOCK PARA SIMULAR A ESTRUTURA ANINHADA (Cliente e Livro) ---

    // DTOs de requisição para espelhar a estrutura da entidade
    public static record MockCliente(Long id) {}
    public static record MockLivro(Long id) {}

    /**
     * DTO de Requisição Simulado, espelhando a estrutura da entidade Aluguel
     */
    public static record MockAluguelRequest(
            MockCliente cliente,
            MockLivro livro
    ) {}

    @BeforeEach
    void setUp() {
        // --- 1. Dados de Requisição (JSON com estrutura aninhada) ---
        MockCliente mockClienteReq = new MockCliente(1L);
        MockLivro mockLivroReq = new MockLivro(101L);
        requestBody = new MockAluguelRequest(mockClienteReq, mockLivroReq);

        // --- MOCKS DE ENTIDADES COMPLETAS (Criando objetos manualmente, já que @Builder não está presente em Cliente) ---

        // 1. Instanciação e preenchimento de Cliente (subclasse de Usuario) usando setters
        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNome("Mock Cliente");
        clienteMock.setEmail("mock@test.com");

        // 2. Instanciação e preenchimento de Livro (assumindo setters)
        Livro livroMock = new Livro();
        livroMock.setId(101L);
        livroMock.setTitulo("Mock Livro");
        livroMock.setAutor("Mock Autor");


        // --- 3. Dados de Retorno (Entidade Aluguel Mockada e COMPLETA) ---
        aluguelRetorno = Aluguel.builder()
                .id(1L)
                .cliente(clienteMock) // CAMPO ESSENCIAL ADICIONADO
                .livro(livroMock)     // CAMPO ESSENCIAL ADICIONADO
                .dataAluguel(LocalDateTime.now())
                .dataDevolucao(LocalDateTime.now().plusDays(7))
                .valorAluguel(10.0)
                .status(biblioteca.onliine.biblioteca.domain.StatusAluguel.ATIVO)
                .build();
    }

    /**
     * Testa o cenário de sucesso na criação de um novo aluguel, alinhando
     * o corpo da requisição com a estrutura aninhada esperada e garantindo
     * que o objeto de retorno seja totalmente serializável.
     */
    @Test
    void testCriarAluguel_Success() throws Exception {
        // Moca o método correto: aluguelService.save(aluguel).
        when(aluguelService.save(
                any(Aluguel.class)
        )).thenReturn(aluguelRetorno);

        // Execução da requisição simulada no endpoint POST /alugueis
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))

                // CORRIGIDO: O Controller foi refatorado para garantir o retorno de 201 Created.
                .andExpect(status().isCreated()) // Revertido para isCreated() (201)

                .andExpect(jsonPath("$.id").value(aluguelRetorno.getId()))
                // Verificações adicionais para garantir que o corpo do JSON foi gerado:
                .andExpect(jsonPath("$.cliente.id").value(aluguelRetorno.getCliente().getId()))
                .andExpect(jsonPath("$.livro.id").value(aluguelRetorno.getLivro().getId()));
    }

    // ----------------------------------------------------
    // TODO: Adicionar testes para validação, autenticação negada, recursos não encontrados, etc.
    // ----------------------------------------------------
}