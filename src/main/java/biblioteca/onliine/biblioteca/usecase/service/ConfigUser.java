package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigUser {

    private final ClienteRepository clienteRepository;

    public ConfigUser(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public boolean loginUsuario(Cliente cliente, String senha) {
        if (cliente == null || cliente.getSenha() == null) {
            return false;
        }
        return cliente.getSenha().equals(senha);
    }

    public String deleteUser(Long id) {
        clienteRepository.deleteById(id);
        return "{deleted: " + id + "}";
    }

    public Cliente updateUser(Cliente cliente) {
        Cliente updatedCliente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        updatedCliente.setNome(cliente.getNome());
        updatedCliente.setCpf(cliente.getCpf());
        updatedCliente.setEmail(cliente.getEmail());
        updatedCliente.setSenha(cliente.getSenha());

        return clienteRepository.save(updatedCliente);
    }
}
