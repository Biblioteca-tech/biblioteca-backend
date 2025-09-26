package biblioteca.onliine.biblioteca.domain.entity;


import biblioteca.onliine.biblioteca.domain.TipoOperacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne // muitos itens podem estar ligados a um mesmo livro
    @JoinColumn(name = "livro_id")
    private Livro livro;

    @ManyToOne // muitos itens podem pertencer a um mesmo cliente
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    private TipoOperacao tipoOperacao; // COMPRA ou ALUGUEL

    @ManyToOne
    @JoinColumn(name = "carrinho_id")
    private Carrinho carrinho;

    private int quantidade;

    private double valorUnitario;
    private double valorTotal;
}
