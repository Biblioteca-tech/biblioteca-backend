package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LivroController livroController;

    private Livro livro;

    @BeforeEach
    void setup() {
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setPreco(50.0);
        livro.setStatusLivro(Status.ATIVO);

        try {
            java.lang.reflect.Field field = LivroController.class.getDeclaredField("diretorio");
            field.setAccessible(true);
            field.set(livroController, System.getProperty("java.io.tmpdir") + File.separator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void deveCadastrarLivroComSucesso() throws IOException {
        String livroJson = "{\"titulo\":\"Livro Teste\",\"preco\":50}";
        MultipartFile capa = new MockMultipartFile("capa", "capa.png", "image/png", "fake".getBytes());
        MultipartFile pdf = new MockMultipartFile("pdf", "livro.pdf", "application/pdf", "fake".getBytes());

        when(objectMapper.readValue(livroJson, Livro.class)).thenReturn(livro);

        ResponseEntity<String> response = livroController.cadastrarLivro(livroJson, capa, pdf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Livro cadastrado com sucesso!", response.getBody());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveRetornarErroAoCadastrarLivroComPrecoNegativo() throws IOException {
        livro.setPreco(-10.0);
        String livroJson = "{\"titulo\":\"Livro Invalido\",\"preco\":-10}";
        MultipartFile capa = new MockMultipartFile("capa", "capa.png", "image/png", "fake".getBytes());
        MultipartFile pdf = new MockMultipartFile("pdf", "livro.pdf", "application/pdf", "fake".getBytes());

        when(objectMapper.readValue(livroJson, Livro.class)).thenReturn(livro);

        ResponseEntity<String> response = livroController.cadastrarLivro(livroJson, capa, pdf);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Valor nao pode ser negativo", response.getBody());
        verify(livroRepository, never()).save(any());
    }


    @Test
    void deveDeletarLivroComSucesso() {
        when(livroService.delete(1L)).thenReturn(ResponseEntity.ok("Livro deletado!"));
        ResponseEntity<String> response = livroController.deletarLivro(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(livroService, times(1)).delete(1L);
    }


    @Test
    void deveAlternarStatusDoLivroDeAtivoParaInativo() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        ResponseEntity<?> response = livroController.toggleStatus(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Status.INATIVO, ((Livro) response.getBody()).getStatusLivro());
        verify(livroRepository, times(1)).save(any());
    }
}
