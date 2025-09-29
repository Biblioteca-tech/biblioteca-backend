package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteService;

    public Cliente findByNome(String nome) {
        return clienteService.findByNome(nome);
    }
    public List<Cliente> findAll() {
        return clienteService.findAll();
    }
    public Cliente findByEmail(String email) {
        return clienteService.findByEmail(email);
    }
    public Cliente findByCpf(String cpf) {
        return clienteService.findByCpf(cpf);
    }
    public Cliente save(Cliente cliente) {
        return clienteService.save(cliente);
    }

}
