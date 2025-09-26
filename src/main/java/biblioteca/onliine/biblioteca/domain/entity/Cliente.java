package biblioteca.onliine.biblioteca.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente extends Pessoa {
    //cliente vai add livro no carrinho
    //se livro comprado vai para o lugar do cliente
}
