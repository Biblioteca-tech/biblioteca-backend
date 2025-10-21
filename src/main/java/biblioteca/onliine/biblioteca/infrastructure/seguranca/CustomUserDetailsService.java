package biblioteca.onliine.biblioteca.infrastructure.seguranca;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.security.core.userdetails.UserDetailsService;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UsernameNotFoundException(email);
        }

        // Suponto que Cliente tenha um método getRoles() que retorna Set<String> ou List<String>
        List<SimpleGrantedAuthority> authorities = cliente.getRoles().stream()
                .map(SimpleGrantedAuthority::new) // cada role precisa ser "ROLE_ADMIN", "ROLE_FUNCIONARIO", etc
                .toList();

        return new User(
                cliente.getEmail(),
                cliente.getSenha(),
                authorities
        );
    }

}
