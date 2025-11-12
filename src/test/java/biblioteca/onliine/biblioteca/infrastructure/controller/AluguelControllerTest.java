package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AluguelControllerTest {

    @Mock
    private AluguelService aluguelService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private LivroService livroService;

    @Mock
    private AluguelRepository aluguelRepository;

    @InjectMocks
    private AluguelController aluguelController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------
    // GET /alugueis
    // -------------------------------------------------------------
    @Test
    void deveListarTodosAlugueisAtivos() {
        List<Aluguel> alugueis = List.of(new Aluguel(), new Aluguel());
        when(aluguelService.findAllAtivos()).thenReturn(alugueis);

        ResponseEntity<List<Aluguel>> resposta = aluguelController.listarTodosAtivos();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(2, resposta.getBody().size());
        verify(aluguelService, times(1)).findAllAtivos();
    }

    // -------------------------------------------------------------
    // GET /alugueis/cliente/{clienteId}
    // -------------------------------------------------------------
    @Test
    void deveRetornarAlugueisDoCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        List<Aluguel> alugueis = List.of(new Aluguel(), new Aluguel());
        when(clienteService.findById(1L)).thenReturn(Optional.of(cliente));
        when(aluguelService.findByCliente(cliente)).thenReturn(alugueis);

        ResponseEntity<?> resposta = aluguelController.listarAlugueisPorCliente(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(alugueis, resposta.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste() {
        when(clienteService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> resposta = aluguelController.listarAlugueisPorCliente(999L);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertEquals("Cliente não encontrado", resposta.getBody());
    }

    // -------------------------------------------------------------
    // POST /alugueis
    // -------------------------------------------------------------
    @Test
    void deveCriarAluguel() {
        Aluguel aluguel = new Aluguel();
        Aluguel salvo = new Aluguel();
        salvo.setId(1L);

        when(aluguelService.save(aluguel)).thenReturn(salvo);

        ResponseEntity<?> resposta = aluguelController.criarAluguel(aluguel);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(salvo, resposta.getBody());
    }

    // -------------------------------------------------------------
    // POST /alugueis/alugar
    // -------------------------------------------------------------
    @Test
    void deveAlugarLivroComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(10L);

        Aluguel aluguel = new Aluguel();
        when(clienteService.findByEmail("email@teste.com")).thenReturn(Optional.of(cliente));
        when(aluguelService.alugarLivro(10L, 5L, 7)).thenReturn(aluguel);

        ResponseEntity<?> resposta = aluguelController.alugarLivro("email@teste.com", 5L, 7);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(aluguel, resposta.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoAlugar() {
        when(clienteService.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        ResponseEntity<?> resposta = aluguelController.alugarLivro("naoexiste@teste.com", 5L, 7);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertEquals("Cliente não encontrado", resposta.getBody());
    }

    @Test
    void deveRetornarBadRequestQuandoFalhaAoAlugar() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteService.findByEmail("teste@teste.com")).thenReturn(Optional.of(cliente));
        when(aluguelService.alugarLivro(1L, 5L, 7)).thenThrow(new RuntimeException("Livro indisponível"));

        ResponseEntity<?> resposta = aluguelController.alugarLivro("teste@teste.com", 5L, 7);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("Livro indisponível", resposta.getBody());
    }

    // -------------------------------------------------------------
    // DELETE /alugueis/deletar-historico/{id}
    // -------------------------------------------------------------
    @Test
    void deveDeletarAluguelQuandoExiste() {
        when(aluguelRepository.existsById(1L)).thenReturn(true);

        aluguelController.deletarAluguel(1L);

        verify(aluguelRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoAluguelNaoExiste() {
        when(aluguelRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> aluguelController.deletarAluguel(1L));
        assertEquals("Aluguel não encontrado para exclusão", ex.getMessage());
    }

    // -------------------------------------------------------------
    // GET /alugueis/historico-aluguel
    // -------------------------------------------------------------
    @Test
    void deveListarHistoricoDeAlugueis() {
        List<Aluguel> historico = List.of(new Aluguel());
        when(aluguelService.listarHistorico()).thenReturn(historico);

        ResponseEntity<List<Aluguel>> resposta = aluguelController.listarHistoricoAluguel();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(historico, resposta.getBody());
        verify(aluguelService, times(1)).listarHistorico();
    }
}
