package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.TipoOperacao;
import biblioteca.onliine.biblioteca.domain.entity.Carrinho;
import biblioteca.onliine.biblioteca.domain.entity.ItemCarrinho;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.CarrinhoRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ItemCarrinhoRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final LivroRepository livroRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, LivroRepository livroRepository, ItemCarrinhoRepository itemCarrinhoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.livroRepository = livroRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
    }

    @Transactional
    public ItemCarrinho adicionarItem(Long carrinhoId, Long livroId, int quantidade, TipoOperacao tipoOperacao) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        ItemCarrinho item = new ItemCarrinho();
        item.setCarrinho(carrinho);
        item.setLivro(livro);
        item.setQuantidade(quantidade);
        item.setTipoOperacao(tipoOperacao);

        double precoBase = livro.getPreco();
        double valorTotal;

        if (tipoOperacao == TipoOperacao.ALUGUEL) {
            valorTotal = precoBase * 0.3 * quantidade; // 30% do preço
        } else {
            valorTotal = precoBase * quantidade;
        }

        item.setValorUnitario(precoBase);
        item.setValorTotal(valorTotal);

        itemCarrinhoRepository.save(item);

        carrinho.setValorTotal(carrinho.getValorTotal() + valorTotal);
        carrinhoRepository.save(carrinho);

        return item;
    }
}
