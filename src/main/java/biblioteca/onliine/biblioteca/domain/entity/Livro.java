package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.FormatoLivro;
import biblioteca.onliine.biblioteca.domain.GeneroLivro;
import biblioteca.onliine.biblioteca.domain.IdiomaLivro;
import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.port.repository.Ativavel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Livro implements Ativavel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String editora;
    private int ano_publicacao;
    private GeneroLivro genero;
    private String sinopse;
    private IdiomaLivro idioma;
    private Double preco;
    private String capaPath;
    private String pdfPath;

    private Status status = Status.ATIVO;

    @Override
    public void ativar() {
        this.status = Status.ATIVO;
    }
    @Override
    public void desativar() {
        this.status = Status.INATIVO;
    }
    @Override
    public boolean isAtivo() {
        return this.status == Status.ATIVO;
    }
}
