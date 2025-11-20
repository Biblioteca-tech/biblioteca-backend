package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveAlterarStatusDoUsuarioComSucesso() {
        // Arrange
        Long usuarioId = 1L;

        Usuario usuario = new Cliente();
        usuario.setId(usuarioId);
        usuario.setEstadoRegistroCliente(EstadoRegistro.ATIVO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<String> resposta = usuarioController.alternarStatusUsuario(usuarioId, EstadoRegistro.INATIVO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Status do usuário 1 alterado para INATIVO", resposta.getBody());
        assertEquals(EstadoRegistro.INATIVO, usuario.getEstadoRegistroCliente());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistir() {
        Long usuarioId = 99L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        ResponseEntity<String> resposta = usuarioController.alternarStatusUsuario(usuarioId, EstadoRegistro.ATIVO);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertEquals("Usuário não encontrado.", resposta.getBody());
        verify(usuarioRepository, never()).save(any());
    }
}
