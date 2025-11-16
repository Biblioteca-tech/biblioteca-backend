package biblioteca.onliine.biblioteca.domain.dto;

import java.util.List;

public record ClienteDTO(
        Long id,
        String nome,
        List<AluguelDTO> alugueis
        ) {
}
