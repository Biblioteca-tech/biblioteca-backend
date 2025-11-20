package biblioteca.onliine.biblioteca.domain.dto;

public record LivroResponseDTO(
    Long id,
    String titulo,
    String autor,
    String editora,
    int ano_publicacao,
    String genero,
    String sinopse,
    String idioma,
    String capaPath,
    Double preco,
    String pdfPath
){}
