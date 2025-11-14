package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.FuncionarioInputDTO;
import biblioteca.onliine.biblioteca.domain.entity.*;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.usecase.service.FuncionarioService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionarioControllerTest {

    @Mock
    private LivroService livroService;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private AluguelRepository aluguelRepository;
    @Mock
    private LivroRepository livroRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        funcionarioController = new FuncionarioController(
                livroService,
                clienteRepository,
                aluguelRepository,
                livroRepository,
                funcionarioRepository,
                passwordEncoder,
                funcionarioService
        );
    }

    @Test
    void deveAtualizarDadosFuncionarioComSucesso() {
        FuncionarioInputDTO dto = new FuncionarioInputDTO();
        dto.setSenha("novaSenha");

        Funcionario funcionario = mock(Funcionario.class);
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("HASH123");

        ResponseEntity<String> response = funcionarioController.atualizarDadosFuncionario(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dados do funcionário atualizados com sucesso!", response.getBody());
        verify(funcionarioRepository).save(funcionario);
        assertEquals("HASH123", dto.getSenha());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarFuncionarioInexistente() {
        when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

        FuncionarioInputDTO dto = new FuncionarioInputDTO();
        ResponseEntity<String> response = funcionarioController.atualizarDadosFuncionario(99L, dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado.", response.getBody());
    }

    @Test
    void deveDeletarFuncionarioComSucesso() {
        doNothing().when(funcionarioService).deletarFuncionario(1L);

        ResponseEntity<String> response = funcionarioController.deletarFuncionario(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Funcionário deletado com sucesso!", response.getBody());
        verify(funcionarioService).deletarFuncionario(1L);
    }

    @Test
    void deveRetornarNotFoundAoDeletarFuncionarioInexistente() {
        doThrow(new RuntimeException("Funcionário não encontrado"))
                .when(funcionarioService).deletarFuncionario(99L);

        ResponseEntity<String> response = funcionarioController.deletarFuncionario(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }

    @Test
    void deveAtualizarLivroQuandoNaoEncontrado() {
        Livro livro = new Livro();
        livro.setTitulo("Teste");

        when(livroService.findById(1L)).thenReturn(Optional.empty());
        when(livroService.update(any(Livro.class))).thenReturn(livro);

        ResponseEntity<Livro> response = funcionarioController.atualizar(1L, livro);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Teste", response.getBody().getTitulo());
    }

    @Test
    void deveRetornarNotFoundQuandoLivroJaExiste() {
        when(livroService.findById(1L)).thenReturn(Optional.of(new Livro()));

        ResponseEntity<Livro> response = funcionarioController.atualizar(1L, new Livro());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deveListarAlugueisDoCliente() {
        Cliente cliente = new Cliente();
        List<Aluguel> alugueis = List.of(new Aluguel());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.findByCliente(cliente)).thenReturn(alugueis);

        ResponseEntity<?> response = funcionarioController.listarAlugueis(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alugueis, response.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = funcionarioController.listarAlugueis(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente não encontrado", response.getBody());
    }

    @Test
    void deveVisualizarPdfComSucesso() throws Exception {
        Livro livro = new Livro();
        livro.setPdfPath("/teste.pdf");

        File arquivo = File.createTempFile("teste", ".pdf");
        arquivo.deleteOnExit();

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        try {
            var field = FuncionarioController.class.getDeclaredField("diretorio");
            field.setAccessible(true);
            field.set(funcionarioController, arquivo.getParent() + "/");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Erro ao definir campo 'diretorio': " + e.getMessage());
        }

        File file = new File(arquivo.getParent() + livro.getPdfPath());
        Files.copy(arquivo.toPath(), file.toPath());

        ResponseEntity<?> response = funcionarioController.visualizarPdf(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("inline"));
        assertTrue(response.getBody() instanceof UrlResource);
    }

    @Test
    void deveRetornarNotFoundQuandoArquivoNaoExiste() throws MalformedURLException {
        Livro livro = new Livro();
        livro.setPdfPath("/inexistente.pdf");
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        try {
            var field = FuncionarioController.class.getDeclaredField("diretorio");
            field.setAccessible(true);
            field.set(funcionarioController, "c:/naoexiste/");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Erro ao definir campo 'diretorio': " + e.getMessage());
        }

        ResponseEntity<?> response = funcionarioController.visualizarPdf(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundQuandoLivroNaoExiste() throws MalformedURLException {
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = funcionarioController.visualizarPdf(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
