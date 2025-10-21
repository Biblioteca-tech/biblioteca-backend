package biblioteca.onliine.biblioteca.infrastructure.seguranca.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email.equals("admin@email.com")) {
            return new User(email, "{noop}1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
        if (email.equals("func@email.com")) {
            return new User(email, "{noop}1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")));
        }
        return new User(email, "{noop}1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE")));
    }
}
