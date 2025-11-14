package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.domain.GeneroLivro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendaControllerTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private VendaController vendaController;

    private Cliente cliente;
    private Livro livro;
    private Venda venda;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setEmail("cliente@teste.com");

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setGenero(GeneroLivro.ROMANCE);
        livro.setPreco(49.90);

        venda = new Venda();
        venda.setCliente(cliente);
        venda.setLivro(livro);
        venda.setValor(livro.getPreco());
        venda.setDataVenda(LocalDateTime.now());
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste() {
        when(clienteRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        ResponseEntity<?> resposta = vendaController.simularVenda("inexistente@teste.com", 1L);

        assertEquals(404, resposta.getStatusCodeValue());
        assertEquals("Cliente não encontrado", resposta.getBody());
        verify(clienteRepository, times(1)).findByEmail("inexistente@teste.com");
    }

    @Test
    void deveRetornarNotFoundQuandoLivroNaoExiste() {
        when(clienteRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(cliente));
        when(livroRepository.findLivroById(1L)).thenReturn(null);

        ResponseEntity<?> resposta = vendaController.simularVenda("cliente@teste.com", 1L);

        assertEquals(404, resposta.getStatusCodeValue());
        assertEquals("Livro não encontrado", resposta.getBody());
        verify(livroRepository, times(1)).findLivroById(1L);
    }

    @Test
    void deveRealizarVendaComSucesso() {
        when(clienteRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(cliente));
        when(livroRepository.findLivroById(1L)).thenReturn(livro);
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);

        ResponseEntity<?> resposta = vendaController.simularVenda("cliente@teste.com", 1L);

        assertEquals(200, resposta.getStatusCodeValue());
        Venda vendaResposta = (Venda) resposta.getBody();
        assertNotNull(vendaResposta);
        assertEquals(cliente, vendaResposta.getCliente());
        assertEquals(livro, vendaResposta.getLivro());
        assertEquals(49.90, vendaResposta.getValor());
        verify(vendaRepository, times(1)).save(any(Venda.class));
    }

    @Test
    void deveGerarRelatorioDeVendasComSucesso() {
        when(vendaRepository.findAll()).thenReturn(List.of(venda));

        ResponseEntity<?> resposta = vendaController.getRelatorio();

        assertEquals(200, resposta.getStatusCodeValue());
        List<Map<String, Object>> relatorio = (List<Map<String, Object>>) resposta.getBody();
        assertNotNull(relatorio);
        assertEquals(1, relatorio.size());
        assertEquals("cliente@teste.com", relatorio.get(0).get("clienteEmail"));
        assertEquals("Livro Teste", relatorio.get(0).get("livroTitulo"));

        assertEquals("ROMANCE", relatorio.get(0).get("livroGenero").toString());
        assertEquals(49.90, relatorio.get(0).get("preco"));
        assertNotNull(relatorio.get(0).get("dataVenda"));
    }
}
