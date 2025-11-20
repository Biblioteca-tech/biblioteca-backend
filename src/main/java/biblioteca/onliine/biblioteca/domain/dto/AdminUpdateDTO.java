package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.GeneroPessoa;
import java.time.LocalDate;

public record AdminUpdateDTO(
        Long id,
        String nome,
        String email,
        String senha,
        GeneroPessoa genero,
        LocalDate data_nascimento,
        String cpf
) {
}
