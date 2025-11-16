package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.GeneroLivro;
import biblioteca.onliine.biblioteca.domain.IdiomaLivro;
import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.port.repository.Ativavel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "livro")
public class Livro implements Ativavel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String editora;
    private int ano_publicacao;

    private GeneroLivro genero;

    @Lob
    private String sinopse;

    @Enumerated(EnumType.STRING)
    private IdiomaLivro idioma;

    @ManyToMany(mappedBy = "livros")
    private List<Cliente> clientes = new ArrayList<>();

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(id, livro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
