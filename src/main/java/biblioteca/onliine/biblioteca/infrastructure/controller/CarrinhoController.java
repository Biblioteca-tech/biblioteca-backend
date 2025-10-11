package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(name = "/carrinho")
public class CarrinhoController {

    private LivroService livroService;

    public CarrinhoController(LivroService livroService) {
        this.livroService = livroService;
    }

    public List<Livro> getLivros() {
        return livroService.findAll();
    }
}
