package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroResponse {
    private boolean sucesso;
    private String mensagem;
    private Cliente cliente;
}
