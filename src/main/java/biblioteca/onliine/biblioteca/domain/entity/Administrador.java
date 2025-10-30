package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id")
public class Administrador extends Usuario {
    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
