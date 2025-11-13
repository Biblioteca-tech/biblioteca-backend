package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Usuario; // Usando a classe abstrata Usuario como tipo
import biblioteca.onliine.biblioteca.domain.entity.Cliente; // Usando Cliente como uma implementação concreta de Usuario
import biblioteca.onliine.biblioteca.domain.port.repository.ComentarioRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    // Objetos de Dados Mock
    private Livro mockLivro;
    private Usuario mockAutor; // Usando a base Usuario

    @BeforeEach
    void setUp() {
        // Inicializa o Livro Mock
        mockLivro = new Livro();
        mockLivro.setId(10L);
        mockLivro.setTitulo("Livro Teste Comentário");

        // Inicializa o Autor Mock (Usando Cliente como implementação concreta de Usuario)
        mockAutor = new Cliente();
        mockAutor.setId(1L);
        mockAutor.setNome("Autor Comentário");
    }

    // ====================================================================
    // TESTE DO MÉTODO: buscarComentariosAtivosPorLivro
    // ====================================================================
    @Test
    void deveBuscarComentariosAtivosComSucesso() {
        // ARRANGE
        Long livroId = 10L;
        List<Comentario> comentariosAtivos = Arrays.asList(new Comentario(), new Comentario());

        when(comentarioRepository.findActiveByLivroId(livroId)).thenReturn(comentariosAtivos);

        // ACT
        List<Comentario> resultado = comentarioService.buscarComentariosAtivosPorLivro(livroId);

        // ASSERT
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());

        // VERIFY
        verify(comentarioRepository, times(1)).findActiveByLivroId(livroId);
    }

    // ====================================================================
    // TESTES DO MÉTODO: adicionarComentario
    // ====================================================================
    @Test
    void deveAdicionarComentarioComSucesso() {
        // ARRANGE
        Long livroId = 10L;
        String textoComentario = "Ótimo livro!";

        // 1. Simula que o Livro é encontrado
        when(livroRepository.findById(livroId)).thenReturn(Optional.of(mockLivro));

        // 2. Captura o objeto Comentario antes de ser salvo
        ArgumentCaptor<Comentario> comentarioCaptor = ArgumentCaptor.forClass(Comentario.class);

        // Simula que o save retorna o objeto capturado (o que acontece na vida real, talvez com um ID gerado)
        when(comentarioRepository.save(comentarioCaptor.capture())).thenAnswer(invocation -> {
            Comentario c = invocation.getArgument(0);
            c.setId(5L); // Simula a geração de ID
            return c;
        });

        // ACT
        Comentario resultado = comentarioService.adicionarComentario(livroId, textoComentario, mockAutor);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(textoComentario, resultado.getTexto());
        assertEquals(mockLivro, resultado.getLivro());
        assertEquals(mockAutor, resultado.getAutor());
        assertNotNull(resultado.getDataCriacao());

        // VERIFY
        verify(livroRepository, times(1)).findById(livroId);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontradoAoAdicionarComentario() {
        // ARRANGE
        Long livroIdInexistente = 99L;
        String textoComentario = "Comentário órfão.";

        // Simula que o Livro NÃO é encontrado
        when(livroRepository.findById(livroIdInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                comentarioService.adicionarComentario(livroIdInexistente, textoComentario, mockAutor)
        );
        assertEquals("Livro não encontrado", exception.getMessage());

        // VERIFY: Garante que o método save NUNCA foi chamado
        verify(comentarioRepository, never()).save(any());
    }

    // ====================================================================
    // TESTES DO MÉTODO: deletarComentario
    // ====================================================================
    @Test
    void deveDeletarComentarioComSucesso() {
        // ARRANGE
        Long comentarioId = 5L;

        // Simula que o Comentário EXISTE
        when(comentarioRepository.existsById(comentarioId)).thenReturn(true);

        // ACT
        assertDoesNotThrow(() -> comentarioService.deletarComentario(comentarioId));

        // VERIFY: Verifica se o existsById foi chamado E se o deleteById também foi chamado
        verify(comentarioRepository, times(1)).existsById(comentarioId);
        verify(comentarioRepository, times(1)).deleteById(comentarioId);
    }

    @Test
    void deveLancarExcecaoQuandoComentarioNaoEncontradoAoDeletar() {
        // ARRANGE
        Long comentarioIdInexistente = 99L;

        // Simula que o Comentário NÃO existe
        when(comentarioRepository.existsById(comentarioIdInexistente)).thenReturn(false);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                comentarioService.deletarComentario(comentarioIdInexistente)
        );
        assertEquals("Comentário não encontrado", exception.getMessage());

        // VERIFY: Garante que deleteById NUNCA foi chamado (falhou na checagem de existência)
        verify(comentarioRepository, times(1)).existsById(comentarioIdInexistente);
        verify(comentarioRepository, never()).deleteById(anyLong());
    }
}