package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;

public record ListarLivrosDTO (
        Long id,
        String titulo,
        String autor,
        String editora,
        Integer anoPublicacao,
        String genero,
        String idioma,
        EstadoRegistro estadoRegistroLivro,
        Double preco,
        String capaPath,
        String pdfPath

){}
