package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final VendaRepository vendaRepository;

    @Value("${spring.diretorio.iarley}")
    private String caminhoPdf;

    private final LivroRepository livroRepository;
    private final ClienteRepository clienteRepository;

    public Livro save(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Optional<Livro> findById(Long id) {
        return livroRepository.findById(id);
    }

    public Livro update(Livro livro) {
        return livroRepository.save(livro);
    }

    public ResponseEntity<String> delete(Long id) {
        boolean livroVinculado = clienteRepository.existsByLivros_Id(id);
        if (livroVinculado) {
            return ResponseEntity
                    .status(400)
                    .body("Não é possível deletar um livro que está conectado a um cliente.");
        }

        Optional<Livro> livro = livroRepository.findById(id);
        if (livro.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Livro não encontrado.");
        }
        livroRepository.deleteById(id);

        return ResponseEntity
                .status(200)
                .body("Livro deletado com sucesso!");
    }

    public List<Livro> findAtivos() {
        return livroRepository.findByEstadoRegistroLivro(EstadoRegistro.ATIVO);
    }

    public File buscarPdf(String nomeArquivoPdf) {
        File arquivo = new File(caminhoPdf + nomeArquivoPdf);
        if (!arquivo.exists()) {
            throw new RuntimeException("PDF não encontrado");
        }

        return arquivo;
    }

    public Map<String, Integer> gerarRelatorioIdiomas() {
        List<Venda> vendas = vendaRepository.findAll();
        Map<String, Integer> contagem = new HashMap<>();

        for (Venda venda : vendas) {
            String idioma = String.valueOf(venda.getLivro().getIdioma());

            contagem.put(idioma, contagem.getOrDefault(idioma, 0) + 1);
        }

        return contagem;
    }

}
