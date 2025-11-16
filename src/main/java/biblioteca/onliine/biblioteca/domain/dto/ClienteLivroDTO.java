package biblioteca.onliine.biblioteca.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteLivroDTO {
    private Long id;
    private Long livroId;
    private String titulo;
    private String autor;
    private String capaPath;
    private String pdfPath;
    private String status;
    private String dataAdicionado;
}
