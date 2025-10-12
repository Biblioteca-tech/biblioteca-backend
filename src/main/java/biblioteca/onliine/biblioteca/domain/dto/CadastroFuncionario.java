package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroFuncionario {
    private boolean sucesso;
    private String mensagem;
    private Funcionario funcionario;
}
