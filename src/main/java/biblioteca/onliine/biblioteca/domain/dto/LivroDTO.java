package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.entity.Livro;

public class LivroDTO {
    public Long id;
    public String titulo;
    public String autor;
    public String capaPath;
    public boolean comprou;

    public LivroDTO(Livro  livro, boolean comprou) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.capaPath = livro.getCapaPath();
        this.comprou = comprou;
    }

}
