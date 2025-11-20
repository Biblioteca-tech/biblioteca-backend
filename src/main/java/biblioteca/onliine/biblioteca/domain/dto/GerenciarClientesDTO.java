package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;

import java.time.LocalDate;

public record GerenciarClientesDTO (
         Long id,
         String nome,
         String email,
         LocalDate data_nascimento,
         String cpf,
         EstadoRegistro estadoRegistroCliente
){
}
