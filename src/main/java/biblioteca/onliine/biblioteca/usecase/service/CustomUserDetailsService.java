package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Convertendo roles da entidade para GrantedAuthority
        Set<GrantedAuthority> authorities = cliente.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                cliente.getEmail(),
                cliente.getSenha(),
                authorities
        );
    }
}
