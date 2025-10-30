package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.GeneroLivro;
import biblioteca.onliine.biblioteca.domain.IdiomaLivro;
import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.port.repository.Ativavel;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    private IdiomaLivro idioma;

    private Double preco;
    private String capaPath;
    private String pdfPath;

    @Enumerated(EnumType.STRING)
    private Status statusLivro = Status.ATIVO;

    @Override
    public void ativar() {
        this.statusLivro = Status.ATIVO;
    }
    @Override
    public void desativar() {
        this.statusLivro = Status.INATIVO;
    }
    @Override
    public boolean isAtivo() {
        return this.statusLivro == Status.ATIVO;
    }

    public void atualizarDadosCompletos(
            String titulo,
            String autor,
            String editora,
            int ano_publicacao,
            GeneroLivro genero,
            String sinopse,
            IdiomaLivro idioma,
            Double preco,
            Status statusLivro
    ) {
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.ano_publicacao = ano_publicacao;
        this.genero = genero;
        this.sinopse = sinopse;
        this.idioma = idioma;
        this.preco = preco;
        this.statusLivro = statusLivro;
    }
}
