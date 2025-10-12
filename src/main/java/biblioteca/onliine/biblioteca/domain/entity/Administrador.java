package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
public class Administrador extends Usuario {
    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
