package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings; // <-- NOVA IMPORTAÇÃO
import org.mockito.quality.Strictness; // <-- NOVA IMPORTAÇÃO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // <-- CORREÇÃO APLICADA
public class CustomUserDetailsServiceTest {

    // Mocks dos Repositórios
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository; // Mockado, mas não precisa de when()

    // Serviço a ser testado
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // Dados Mock
    private final String EMAIL_ATIVO = "ativo@biblioteca.com";
    private final String EMAIL_INATIVO = "inativo@biblioteca.com";
    private final String EMAIL_NAO_EXISTENTE = "naoexiste@biblioteca.com";
    private final String SENHA_HASH = "hashed_password";
    private Usuario mockUsuarioAtivo;
    private Usuario mockUsuarioInativo;

    @BeforeEach
    void setUp() {
        // ARRANGE global: Configura os objetos mock

        // 1. Usuário Ativo (usamos Cliente, uma subclasse concreta de Usuario)
        mockUsuarioAtivo = new Cliente();
        mockUsuarioAtivo.setId(1L);
        mockUsuarioAtivo.setEmail(EMAIL_ATIVO);
        mockUsuarioAtivo.setSenha(SENHA_HASH);
        mockUsuarioAtivo.setStatusCliente(Status.ATIVO);

        // 2. Usuário Inativo
        mockUsuarioInativo = new Cliente();
        mockUsuarioInativo.setId(2L);
        mockUsuarioInativo.setEmail(EMAIL_INATIVO);
        mockUsuarioInativo.setSenha(SENHA_HASH);
        mockUsuarioInativo.setStatusCliente(Status.INATIVO);

        // Configura Mocks de Repositório (Todos os stubs estão no BeforeEach, por isso precisamos do LENIENT)
        when(usuarioRepository.findByEmail(EMAIL_ATIVO)).thenReturn(mockUsuarioAtivo);
        when(usuarioRepository.findByEmail(EMAIL_INATIVO)).thenReturn(mockUsuarioInativo);
        when(usuarioRepository.findByEmail(EMAIL_NAO_EXISTENTE)).thenReturn(null);
    }

    // ====================================================================
    // TESTES DE SUCESSO
    // ====================================================================

    @Test
    void deveCarregarUsuarioAtivoComSucesso() {
        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(EMAIL_ATIVO);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(EMAIL_ATIVO, userDetails.getUsername());
        assertEquals(SENHA_HASH, userDetails.getPassword());

        // VERIFY
        verify(usuarioRepository, times(1)).findByEmail(EMAIL_ATIVO);
    }

    // ====================================================================
    // TESTES DE FALHA (Exceções)
    // ====================================================================

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(EMAIL_NAO_EXISTENTE)
        );

        // ASSERT
        assertEquals("Usuário não encontrado com email: " + EMAIL_NAO_EXISTENTE, exception.getMessage());

        // VERIFY
        verify(usuarioRepository, times(1)).findByEmail(EMAIL_NAO_EXISTENTE);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(EMAIL_INATIVO)
        );

        // ASSERT
        assertEquals("Usuário inativo: " + EMAIL_INATIVO, exception.getMessage());

        // VERIFY
        verify(usuarioRepository, times(1)).findByEmail(EMAIL_INATIVO);
    }
}