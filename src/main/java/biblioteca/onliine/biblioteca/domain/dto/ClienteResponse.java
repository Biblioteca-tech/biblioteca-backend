package biblioteca.onliine.biblioteca.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private String nome;
    private String email;
}
