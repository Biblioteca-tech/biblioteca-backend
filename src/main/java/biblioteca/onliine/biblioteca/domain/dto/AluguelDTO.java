package biblioteca.onliine.biblioteca.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AluguelDTO(
        Long id,
        LivrosDTO livro,
        LocalDateTime dataAluguel
) {
}
