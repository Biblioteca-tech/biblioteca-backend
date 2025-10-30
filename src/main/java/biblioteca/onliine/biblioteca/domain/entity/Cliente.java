package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {

    // Campo statusCliente removido daqui

    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
