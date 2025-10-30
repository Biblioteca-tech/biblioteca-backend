package biblioteca.onliine.biblioteca.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class FuncionarioInputDTO {
    private String nome;
    private String email;
    private Date data_nascimento;
    private String cpf;
    private String senha; // Adicionado para a atualização completa

    private LocalDate dataAdmissao;
    private String numeroTelefone;
    private String endereco;
}
