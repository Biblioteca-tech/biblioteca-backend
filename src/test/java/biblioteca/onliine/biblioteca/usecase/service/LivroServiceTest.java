package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {

    // Mock do Repositório
    @Mock
    private LivroRepository livroRepository;

    // Serviço a ser testado
    @InjectMocks
    private LivroService livroService;

    // Dados Mock
    private Livro mockLivro;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;

    @BeforeEach
    void setUp() {
        // ARRANGE global: Cria um livro padrão para os testes
        mockLivro = new Livro();
        mockLivro.setId(ID_EXISTENTE);
        mockLivro.setTitulo("Aventura Teste");
        mockLivro.setEstadoRegistroLivro(EstadoRegistro.ATIVO);
    }

    // ====================================================================
    // TESTES DO MÉTODO: save
    // ====================================================================
    @Test
    void deveSalvarLivroComSucesso() {
        // ARRANGE
        when(livroRepository.save(mockLivro)).thenReturn(mockLivro);

        // ACT
        Livro resultado = livroService.save(mockLivro);

        // ASSERT
        assertNotNull(resultado);

        // VERIFY
        verify(livroRepository, times(1)).save(mockLivro);
    }

    // ====================================================================
    // TESTES DO MÉTODO: findAll e findAtivos
    // ====================================================================
    @Test
    void deveRetornarTodosOsLivros() {
        // ARRANGE
        List<Livro> lista = Arrays.asList(mockLivro, new Livro());
        when(livroRepository.findAll()).thenReturn(lista);

        // ACT
        List<Livro> resultado = livroService.findAll();

        // ASSERT
        assertEquals(2, resultado.size());

        // VERIFY
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarLivrosAtivos() {
        // ARRANGE
        List<Livro> listaAtivos = Collections.singletonList(mockLivro);
        when(livroRepository.findByEstadoRegistroLivro(EstadoRegistro.ATIVO)).thenReturn(listaAtivos);

        // ACT
        List<Livro> resultado = livroService.findAtivos();

        // ASSERT
        assertEquals(1, resultado.size());

        // VERIFY
        verify(livroRepository, times(1)).findByEstadoRegistroLivro(EstadoRegistro.ATIVO);
    }

    // ====================================================================
    // TESTES DO MÉTODO: findById
    // ====================================================================
    @Test
    void deveRetornarLivroPorIdExistente() {
        // ARRANGE
        when(livroRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockLivro));

        // ACT
        Optional<Livro> resultado = livroService.findById(ID_EXISTENTE);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(mockLivro, resultado.get());

        // VERIFY
        verify(livroRepository, times(1)).findById(ID_EXISTENTE);
    }

    // ====================================================================
    // TESTES DO MÉTODO: delete
    // ====================================================================

    @Test
    void deveDeletarLivroComSucessoQuandoEncontrado() {
        // ARRANGE
        when(livroRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(mockLivro));

        // ACT
        ResponseEntity<String> response = livroService.delete(ID_EXISTENTE);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Livro deletado com sucesso!", response.getBody());

        // VERIFY: Verifica se findById foi chamado E se delete foi chamado
        verify(livroRepository, times(1)).findById(ID_EXISTENTE);
        verify(livroRepository, times(1)).delete(mockLivro);
    }

    @Test
    void deveRetornarNotFoundQuandoLivroNaoEncontradoAoDeletar() {
        // ARRANGE
        when(livroRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<String> response = livroService.delete(ID_INEXISTENTE);

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // VERIFY: Verifica se findById foi chamado E se delete NUNCA foi chamado
        verify(livroRepository, times(1)).findById(ID_INEXISTENTE);
        verify(livroRepository, never()).delete(any(Livro.class));
    }
}