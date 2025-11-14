package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.ComentarioDTO;
import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import biblioteca.onliine.biblioteca.usecase.service.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComentarioControllerTest {

    @Mock
    private ComentarioService comentarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ComentarioController comentarioController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarComentariosDeLivroComSucesso() {
        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setTexto("Excelente livro!");

        when(comentarioService.buscarComentariosAtivosPorLivro(10L))
                .thenReturn(List.of(comentario));

        ResponseEntity<List<ComentarioDTO>> response = comentarioController.getComentariosPorLivro(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Excelente livro!", response.getBody().get(0).getTexto());
    }

    @Test
    void deveRetornarNoContentQuandoNaoExistiremComentarios() {
        when(comentarioService.buscarComentariosAtivosPorLivro(10L))
                .thenReturn(List.of());

        ResponseEntity<List<ComentarioDTO>> response = comentarioController.getComentariosPorLivro(10L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
    

    @Test
    void naoDeveAdicionarComentarioQuandoTextoVazio() {
        UserDetails userDetails = new User("user@email.com", "123", List.of());

        Map<String, String> body = Map.of("texto", "");
        ResponseEntity<ComentarioDTO> response = comentarioController.adicionarComentario(5L, body, userDetails);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(comentarioService, never()).adicionarComentario(anyLong(), anyString(), any());
    }

    @Test
    void deveDeletarComentarioComSucesso() {
        doNothing().when(comentarioService).deletarComentario(1L);

        ResponseEntity<String> response = comentarioController.deletarComentario(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Comentário deletado com sucesso.", response.getBody());
        verify(comentarioService).deletarComentario(1L);
    }

    @Test
    void deveRetornarNotFoundQuandoComentarioNaoExistir() {
        doThrow(new RuntimeException("Comentário não encontrado"))
                .when(comentarioService).deletarComentario(1L);

        ResponseEntity<String> response = comentarioController.deletarComentario(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Comentário não encontrado", response.getBody());
        verify(comentarioService).deletarComentario(1L);
    }
}
