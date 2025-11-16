package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.Status;

public record ListarLivrosDTO(
        Long id,
        String titulo,
        String autor,
        String editora,
        Integer anoPublicacao,
        String genero,
        String idioma,
        Status statusLivro,
        Double preco,
        String capaPath,
        String pdfPath

){}
