package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.Status;

import java.time.LocalDate;
import java.util.Date;

public record GerenciarClientesDTO (
         Long id,
         String nome,
         String email,
         LocalDate data_nascimento,
         String cpf,
         Status statusCliente
){
}
