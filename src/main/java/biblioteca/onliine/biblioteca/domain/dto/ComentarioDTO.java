package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ComentarioDTO {
    private Long id;
    private String texto;
    private Date dataCriacao;
    private String nomeAutor; // Retorna apenas o nome do autor

    // Construtor que converte a entidade Comentario para o DTO
    public ComentarioDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.texto = comentario.getTexto();
        this.dataCriacao = comentario.getDataCriacao();
        // Usa o método auxiliar que criamos na entidade
        this.nomeAutor = comentario.getNomeAutor();
    }
}
