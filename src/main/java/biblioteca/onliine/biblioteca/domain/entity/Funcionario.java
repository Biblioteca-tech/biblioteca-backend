package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "funcionario")
@PrimaryKeyJoinColumn(name = "id")
public class Funcionario extends Usuario {
    private LocalDate dataAdmissao;
    private String numeroTelefone;
    private String endereco;

    @Override
    public void atualizarSenha(String senha) {
        this.setSenha(senha);
    }
}
