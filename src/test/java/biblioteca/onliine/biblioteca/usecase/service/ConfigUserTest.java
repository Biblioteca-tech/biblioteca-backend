package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfigUserTest {

    // 1. Mock: Simula o repositório
    @Mock
    private ClienteRepository clienteRepository;

    // 2. InjectMocks: Cria a instância real do serviço
    @InjectMocks
    private ConfigUser configUser;

    // Objeto de dados para reuso
    private Cliente mockCliente;
    private final String SENHA_CORRETA = "senha123";
    private final String SENHA_INCORRETA = "errada";

    @BeforeEach
    void setUp() {
        // ARRANGE global: Cria um cliente padrão com uma senha definida
        mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNome("Alice");
        mockCliente.setEmail("alice@test.com");
        mockCliente.setSenha(SENHA_CORRETA);
        mockCliente.setCpf("111.111.111-11");
    }

    // ====================================================================
    // TESTES DO MÉTODO: buscarPorEmail
    // ====================================================================
    @Test
    void deveBuscarClientePorEmailComSucesso() {
        // ARRANGE
        when(clienteRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(mockCliente));

        // ACT
        Optional<Cliente> resultado = configUser.buscarPorEmail("alice@test.com");

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(mockCliente, resultado.get());

        // VERIFY
        verify(clienteRepository, times(1)).findByEmail("alice@test.com");
    }

    // ====================================================================
    // TESTES DO MÉTODO: loginUsuario
    // ====================================================================

    @Test
    void deveRetornarTrueParaLoginComSenhaCorreta() {
        // ACT & ASSERT
        assertTrue(configUser.loginUsuario(mockCliente, SENHA_CORRETA));
    }

    @Test
    void deveRetornarFalseParaLoginComSenhaIncorreta() {
        // ACT & ASSERT
        assertFalse(configUser.loginUsuario(mockCliente, SENHA_INCORRETA));
    }

    @Test
    void deveRetornarFalseQuandoClienteForNulo() {
        // ACT & ASSERT
        assertFalse(configUser.loginUsuario(null, SENHA_CORRETA));
    }

    @Test
    void deveRetornarFalseQuandoSenhaDoClienteForNula() {
        // ARRANGE: Cria um cliente com senha nula
        mockCliente.setSenha(null);

        // ACT & ASSERT
        assertFalse(configUser.loginUsuario(mockCliente, SENHA_CORRETA));
    }

    // ====================================================================
    // TESTES DO MÉTODO: deleteUser
    // ====================================================================
    @Test
    void deveChamarDeleteByIdERetornarMensagemDeSucesso() {
        // ARRANGE
        Long clienteId = 1L;

        // ACT
        String resultado = configUser.deleteUser(clienteId);

        // ASSERT
        assertEquals("{deleted: 1}", resultado);

        // VERIFY: Garante que o método de deleção do repositório foi chamado
        verify(clienteRepository, times(1)).deleteById(clienteId);
    }

    // ====================================================================
    // TESTES DO MÉTODO: updateUser
    // ====================================================================
    @Test
    void deveAtualizarClienteComSucesso() {
        // ARRANGE: Cliente com dados modificados
        Cliente clienteComNovosDados = new Cliente();
        clienteComNovosDados.setId(1L);
        clienteComNovosDados.setNome("Alice Nova");
        clienteComNovosDados.setCpf("222.222.222-22");
        clienteComNovosDados.setEmail("nova.alice@test.com");
        clienteComNovosDados.setSenha("nova_senha456"); // Senha também atualizada

        // 1. Simula que o cliente original é encontrado
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));

        // 2. Simula que o repositório retorna o cliente atualizado (o capturado)
        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        when(clienteRepository.save(clienteCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Cliente resultado = configUser.updateUser(clienteComNovosDados);

        // ASSERT
        assertNotNull(resultado);

        // Pega o objeto que foi passado para o save e verifica a atualização
        Cliente clienteSalvo = clienteCaptor.getValue();
        assertEquals("Alice Nova", clienteSalvo.getNome());
        assertEquals("222.222.222-22", clienteSalvo.getCpf());
        assertEquals("nova.alice@test.com", clienteSalvo.getEmail());
        assertEquals("nova_senha456", clienteSalvo.getSenha());

        // O cliente original (mockCliente) deve ter sido modificado (efeito colateral ok, mas o resultado é o que importa)
        assertEquals(clienteComNovosDados.getNome(), resultado.getNome());

        // VERIFY
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontradoAoAtualizar() {
        // ARRANGE
        Cliente clienteInexistente = new Cliente();
        clienteInexistente.setId(99L); // ID inexistente

        // Simula que o cliente não é encontrado
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                configUser.updateUser(clienteInexistente)
        );
        assertEquals("Cliente não encontrado", exception.getMessage());

        // VERIFY: Garante que o save NUNCA foi chamado
        verify(clienteRepository, times(1)).findById(99L);
        verify(clienteRepository, never()).save(any());
    }
}