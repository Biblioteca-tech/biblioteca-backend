package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
