package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.GeneroPessoa;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioInputDTO {
    private String nome;
    private String email;
    private LocalDate data_nascimento;
    private String cpf;
    private String senha;

    private LocalDate dataAdmissao;
    private String numeroTelefone;
    private String endereco;
    private GeneroPessoa genero;
}
