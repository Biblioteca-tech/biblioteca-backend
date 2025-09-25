package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.FormatoLivro;
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
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String editora;
    private int ano_publicacao;
    private String genero;
    private String sinopse;
    private String idioma;
    private double preco;

    @Enumerated(EnumType.STRING)
    private FormatoLivro formato;
}
