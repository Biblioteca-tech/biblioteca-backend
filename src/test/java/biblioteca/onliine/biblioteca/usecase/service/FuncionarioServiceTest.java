package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.port.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceTest {

    // Mock: Simula o repositório
    @Mock
    private FuncionarioRepository funcionarioRepository;

    // InjectMocks: Cria a instância real do serviço
    @InjectMocks
    private FuncionarioService funcionarioService;

    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;

    // ====================================================================
    // TESTE DO MÉTODO: deletarFuncionario - FLUXO DE SUCESSO
    // ====================================================================
    @Test
    void deveDeletarFuncionarioComSucessoQuandoExiste() {
        // ARRANGE
        // Simula que o funcionário existe
        when(funcionarioRepository.existsById(ID_EXISTENTE)).thenReturn(true);

        // ACT & ASSERT
        // O método deve ser executado sem lançar exceção
        assertDoesNotThrow(() -> funcionarioService.deletarFuncionario(ID_EXISTENTE));

        // VERIFY: Confirma que os dois métodos do repositório foram chamados na ordem correta
        verify(funcionarioRepository, times(1)).existsById(ID_EXISTENTE);
        verify(funcionarioRepository, times(1)).deleteById(ID_EXISTENTE);
    }

    // ====================================================================
    // TESTE DO MÉTODO: deletarFuncionario - FLUXO DE FALHA
    // ====================================================================
    @Test
    void deveLancarExcecaoQuandoFuncionarioNaoExiste() {
        // ARRANGE
        // Simula que o funcionário NÃO existe
        when(funcionarioRepository.existsById(ID_INEXISTENTE)).thenReturn(false);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                funcionarioService.deletarFuncionario(ID_INEXISTENTE)
        );

        // ASSERT: Verifica a mensagem de exceção
        assertEquals("Funcionário não encontrado.", exception.getMessage());

        // VERIFY: Garante que a deleção NUNCA foi tentada
        verify(funcionarioRepository, times(1)).existsById(ID_INEXISTENTE);
        verify(funcionarioRepository, never()).deleteById(anyLong());
    }
}