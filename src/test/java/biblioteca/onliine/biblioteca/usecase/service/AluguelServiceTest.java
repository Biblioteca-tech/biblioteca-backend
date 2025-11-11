package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AluguelServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private AluguelService aluguelService;

    private Cliente mockCliente;
    private Livro mockLivro;

    @BeforeEach
    void setUp() {
        // CORREÇÃO APLICADA: Usando new() e setters() em vez de builder()
        // para Cliente e Livro, conforme suas entidades.

        mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNome("João Teste");

        mockLivro = new Livro();
        mockLivro.setId(10L);
        mockLivro.setTitulo("Livro Teste");
        mockLivro.setPreco(20.0);
    }

    // ====================================================================
    // TESTES DO MÉTODO: alugarLivro
    // ====================================================================

    @Test
    void deveAlugarLivroComSucesso() {
        // ARRANGE
        Long clienteId = 1L;
        Long livroId = 10L;
        int dias = 5; // O método do service atualmente ignora este parâmetro

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(mockCliente));
        when(livroRepository.findLivroById(livroId)).thenReturn(mockLivro);

        ArgumentCaptor<Aluguel> aluguelCaptor = ArgumentCaptor.forClass(Aluguel.class);

        // Quando o save for chamado, simula o retorno do objeto capturado
        // (Isso é necessário pois o Aluguel só é totalmente construído dentro do service)
        when(aluguelRepository.save(aluguelCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));


        // ACT
        Aluguel resultado = aluguelService.alugarLivro(clienteId, livroId, dias);

        assertNotNull(resultado);
        assertEquals(StatusAluguel.ATIVO, resultado.getStatus());
        assertEquals(mockCliente, resultado.getCliente());
        assertEquals(mockLivro, resultado.getLivro());
        assertEquals(20.0, resultado.getValorAluguel());

        verify(aluguelRepository, times(1)).save(any(Aluguel.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        Long clienteInexistenteId = 99L;
        when(clienteRepository.findById(clienteInexistenteId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                aluguelService.alugarLivro(clienteInexistenteId, 10L, 5)
        );
        assertEquals("Cliente não encontrado", exception.getMessage());

        verify(aluguelRepository, never()).save(any());
        verify(livroRepository, never()).findLivroById(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        // ARRANGE
        Long clienteId = 1L;
        Long livroInexistenteId = 99L;

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(mockCliente));
        when(livroRepository.findLivroById(livroInexistenteId)).thenReturn(null);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                aluguelService.alugarLivro(clienteId, livroInexistenteId, 5)
        );
        assertEquals("Livro não encontrado", exception.getMessage());

        // VERIFY
        verify(aluguelRepository, never()).save(any());
    }

    // ====================================================================
    // TESTES DO MÉTODO: verificarAlugueisVencidos (@Scheduled)
    // ====================================================================

    @Test
    void deveFinalizarAlugueisAtrasadosAtivos() {
        // ARRANGE
        LocalDateTime dataDevolucaoPassada = LocalDateTime.now().minusMinutes(5);

        // 1. Aluguel ATIVO e ATRASADO (Deve ser FINALIZADO)
        Aluguel atrasadoAtivo = Aluguel.builder()
                .id(1L)
                .status(StatusAluguel.ATIVO)
                .dataDevolucao(dataDevolucaoPassada)
                .build();

        // 2. Aluguel ATIVO e NÃO ATRASADO (Data de devolução no futuro)
        Aluguel naoAtrasadoAtivo = Aluguel.builder()
                .id(2L)
                .status(StatusAluguel.ATIVO)
                .dataDevolucao(LocalDateTime.now().plusHours(1))
                .build();

        // 3. Aluguel FINALIZADO (Deve ser ignorado)
        Aluguel jaFinalizado = Aluguel.builder()
                .id(3L)
                .status(StatusAluguel.FINALIZADO) // (StatusAluguel.DEVOLVIDO ou FINALIZADO)
                .dataDevolucao(dataDevolucaoPassada)
                .build();

        List<Aluguel> listaAlugueis = Arrays.asList(atrasadoAtivo, naoAtrasadoAtivo, jaFinalizado);

        when(aluguelRepository.findAll()).thenReturn(listaAlugueis);

        // ACT
        aluguelService.verificarAlugueisVencidos();

        // ASSERT & VERIFY
        ArgumentCaptor<Aluguel> aluguelCaptor = ArgumentCaptor.forClass(Aluguel.class);

        // Deve chamar o save UMA VEZ
        verify(aluguelRepository, times(1)).save(aluguelCaptor.capture());

        Aluguel aluguelSalvo = aluguelCaptor.getValue();
        assertEquals(StatusAluguel.FINALIZADO, aluguelSalvo.getStatus());
        assertEquals(1L, aluguelSalvo.getId());
    }

    @Test
    void naoDeveFinalizarNenhumAluguelSeNaoHouverAtrasos() {
        // ARRANGE
        Aluguel noPrazo = Aluguel.builder()
                .id(1L)
                .status(StatusAluguel.ATIVO)
                .dataDevolucao(LocalDateTime.now().plusMinutes(5))
                .build();

        when(aluguelRepository.findAll()).thenReturn(Collections.singletonList(noPrazo));

        // ACT
        aluguelService.verificarAlugueisVencidos();

        // VERIFY
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    // ====================================================================
    // TESTES DOS MÉTODOS DE BUSCA E SALVAMENTO SIMPLES
    // ====================================================================

    @Test
    void deveRetornarTodosAlugueisAtivos() {
        // ARRANGE
        List<Aluguel> alugueisAtivos = Arrays.asList(new Aluguel(), new Aluguel());
        when(aluguelRepository.findByStatus(StatusAluguel.ATIVO)).thenReturn(alugueisAtivos);

        // ACT
        List<Aluguel> resultado = aluguelService.findAllAtivos();

        // ASSERT & VERIFY
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        verify(aluguelRepository, times(1)).findByStatus(StatusAluguel.ATIVO);
    }
}