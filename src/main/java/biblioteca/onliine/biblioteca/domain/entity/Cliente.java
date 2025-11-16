package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {

    @ManyToMany
    @JoinTable(
            name = "cliente_livros",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "livro_id")
    )
    private List<Livro> livros;


    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
