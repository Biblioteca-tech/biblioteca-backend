package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.dto.AdminUpdateDTO;
import biblioteca.onliine.biblioteca.domain.entity.Administrador;
import biblioteca.onliine.biblioteca.domain.port.repository.AdmmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdmService {
    private final AdmmRepository admmRepository;
    private final PasswordEncoder passwordEncoder;


    public Administrador getPerfilLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return admmRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
    }
    public Administrador atualizarPerfil(AdminUpdateDTO dto) {

        Administrador admin = getPerfilLogado();

        if (dto.nome() != null) {
            admin.setNome(dto.nome());
        }

        if (dto.email() != null) {
            admin.setEmail(dto.email());
        }

        if (dto.genero() != null) {
            admin.setGenero(dto.genero());
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            admin.setSenha(passwordEncoder.encode(dto.senha()));
        }

        return admmRepository.save(admin);
    }
}
