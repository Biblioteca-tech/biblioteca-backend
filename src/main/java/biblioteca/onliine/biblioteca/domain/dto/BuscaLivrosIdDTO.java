package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.GeneroLivro;
import biblioteca.onliine.biblioteca.domain.IdiomaLivro;
import biblioteca.onliine.biblioteca.domain.Status;

public record BuscaLivrosIdDTO (
        Long id,
        String titulo,
        String autor,
        String editora,
        int ano_publicacao,
        GeneroLivro genero,
        String sinopse,
        IdiomaLivro idioma,
        Double preco,
        Status statusLivro,
        String pdfPath,
        String capaPath
){
}
