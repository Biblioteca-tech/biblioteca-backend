package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    // 1. Mock: Simula o repositório
    @Mock
    private ClienteRepository clienteRepository;

    // 2. InjectMocks: Cria a instância real do serviço e injeta o mock acima
    @InjectMocks
    private ClienteService clienteService;

    // Objeto de dados para reuso
    private Cliente mockCliente;

    @BeforeEach
    void setUp() {
        // ARRANGE global: Cria um cliente padrão para os testes
        mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNome("Cliente Teste");
        mockCliente.setEmail("cliente@teste.com");
        mockCliente.setCpf("123.456.789-00");
    }

    @Test
    void deveRetornarTodosOsClientes() {
        // ARRANGE: Configura o mock
        List<Cliente> listaMock = Arrays.asList(mockCliente, new Cliente());
        when(clienteRepository.findAll()).thenReturn(listaMock);

        // ACT
        List<Cliente> resultado = clienteService.findAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        // VERIFY: Garante que o método findAll() do repositório foi chamado 1 vez
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarClientePorId() {
        // ARRANGE
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));

        // ACT
        Optional<Cliente> resultado = clienteService.findById(1L);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(mockCliente, resultado.get());

        // VERIFY
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarClientePorNome() {
        // ARRANGE
        List<Cliente> listaMock = Collections.singletonList(mockCliente);
        when(clienteRepository.findByNome("Cliente Teste")).thenReturn(listaMock);

        // ACT
        List<Cliente> resultado = clienteService.findByNome("Cliente Teste");

        // ASSERT
        assertFalse(resultado.isEmpty());
        assertEquals(mockCliente, resultado.get(0));

        // VERIFY
        verify(clienteRepository, times(1)).findByNome("Cliente Teste");
    }

    @Test
    void deveRetornarClientePorEmail() {
        // ARRANGE
        when(clienteRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(mockCliente));

        // ACT
        Optional<Cliente> resultado = clienteService.findByEmail("cliente@teste.com");

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals("cliente@teste.com", resultado.get().getEmail());

        // VERIFY
        verify(clienteRepository, times(1)).findByEmail("cliente@teste.com");
    }

    @Test
    void deveRetornarClientePorCpf() {
        // ARRANGE
        when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(mockCliente));

        // ACT
        Optional<Cliente> resultado = clienteService.findByCpf("123.456.789-00");

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals("123.456.789-00", resultado.get().getCpf());

        // VERIFY
        verify(clienteRepository, times(1)).findByCpf("123.456.789-00");
    }

    @Test
    void deveSalvarCliente() {
        // ARRANGE
        // Simula que o save() retorna o mesmo cliente que foi passado (agora com ID, por ex.)
        when(clienteRepository.save(mockCliente)).thenReturn(mockCliente);

        // ACT
        Cliente resultado = clienteService.save(mockCliente);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        // VERIFY
        verify(clienteRepository, times(1)).save(mockCliente);
    }
}