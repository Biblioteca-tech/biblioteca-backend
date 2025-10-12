package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfigUser {

    UserRepository userRepository;
    public ConfigUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Cliente buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean loginUsuario(Cliente cliente, String senha) {
        if (cliente == null) {
            return false;
        }
        if (cliente.getSenha() == null) {
            return false;
        }
        return cliente.getSenha().equals(senha);
    }
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "{deleted: " + id + "}";
    }
    public Cliente updateUser(Cliente cliente) {
        Cliente updatedCliente = userRepository.findById(cliente.getId()).orElse(null);
        updatedCliente.setNome(cliente.getNome());
        updatedCliente.setCpf(cliente.getCpf());
        updatedCliente.setEmail(cliente.getEmail());
        updatedCliente.setSenha(cliente.getSenha());
        return userRepository.save(updatedCliente);
    }
}
