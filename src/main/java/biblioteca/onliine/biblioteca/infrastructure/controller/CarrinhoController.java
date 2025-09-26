package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.TipoOperacao;
import biblioteca.onliine.biblioteca.domain.entity.ItemCarrinho;
import biblioteca.onliine.biblioteca.usecase.service.CarrinhoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

    CarrinhoService carrinhoService;
    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping("/carrinho/{id}/adicionar")
    public ResponseEntity<ItemCarrinho> adicionarItem(
            @PathVariable Long id,
            @RequestParam Long livroId,
            @RequestParam int quantidade,
            @RequestParam TipoOperacao tipoOperacao) {

        ItemCarrinho item = carrinhoService.adicionarItem(id, livroId, quantidade, tipoOperacao);
        return ResponseEntity.ok(item);
    }
}
