package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.dto.FuncionarioInputDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "funcionario")
public class Funcionario extends Usuario {
    private LocalDate dataAdmissao;
    private String numeroTelefone;
    private String endereco;

    @Override
    public void atualizarSenha(String senha) {
        // A senha já chega criptografada aqui do Controller
        this.setSenha(senha);
    }

    public void atualizarDados(FuncionarioInputDTO dados) {
        if (dados.getNome() != null) this.setNome(dados.getNome());
        if (dados.getEmail() != null) this.setEmail(dados.getEmail());
        if (dados.getData_nascimento() != null) this.setData_nascimento(dados.getData_nascimento());
        if (dados.getCpf() != null) this.setCpf(dados.getCpf());

        // Lidar com a senha, se fornecida no DTO (já criptografada pelo controller)
        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            this.atualizarSenha(dados.getSenha());
        }

        if (dados.getDataAdmissao() != null) this.dataAdmissao = dados.getDataAdmissao();
        if (dados.getNumeroTelefone() != null) this.numeroTelefone = dados.getNumeroTelefone();
        if (dados.getEndereco() != null) this.endereco = dados.getEndereco();
    }
}
