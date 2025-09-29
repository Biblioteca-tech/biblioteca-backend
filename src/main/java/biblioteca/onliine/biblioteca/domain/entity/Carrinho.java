package biblioteca.onliine.biblioteca.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Livro> itens = new ArrayList<>();

    private double valorTotal = 0.0;

    public void atualizarTotal() {
        this.valorTotal = itens.stream()
                .mapToDouble(e -> e.getPreco() == null ? 0.0 : e.getPreco())
                .sum();
    }
}
