package biblioteca.onliine.biblioteca.dto;

import biblioteca.onliine.biblioteca.model.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroResponse {
    private boolean sucesso;
    private String mensagem;
    private Cliente cliente;
}
