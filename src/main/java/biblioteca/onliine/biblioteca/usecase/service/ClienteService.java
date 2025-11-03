package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Retorna todos os clientes
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    // Busca cliente por ID
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    // Busca cliente por nome
    public List<Cliente> findByNome(String nome) {
        return clienteRepository.findByNome(nome);
    }

    // Busca cliente por e-mail
    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    // Busca cliente por CPF
    public Optional<Cliente> findByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    // Salva um novo cliente
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
