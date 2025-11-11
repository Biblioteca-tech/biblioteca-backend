package biblioteca.onliine.biblioteca.usecase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    // Mock: Simula o componente de envio de e-mail do Spring
    @Mock
    private JavaMailSender mailSender;

    // InjectMocks: Cria a instância real do serviço e injeta o mock
    @InjectMocks
    private EmailService emailService;

    // Captor: Objeto para capturar a mensagem enviada pelo serviço
    private ArgumentCaptor<SimpleMailMessage> mensagemCaptor;

    // Dados Mock para Reuso
    private final String DESTINATARIO = "usuario@teste.com";
    private final String NOME_USUARIO = "Fulaninho de Tal";
    private final String EMAIL_FROM = "medic.projetos@gmail.com";

    @BeforeEach
    void setUp() {
        // Inicializa o ArgumentCaptor antes de cada teste
        mensagemCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // O mailSender.send() não faz nada (void), então não precisamos do when().
        // Apenas precisamos verificar se ele foi chamado.
    }

    // ====================================================================
    // TESTE DO MÉTODO: enviarEmailLogin
    // ====================================================================
    @Test
    void deveConstruirEEnviarEmailLoginCorretamente() {
        // ARRANGE - Nenhum setup complexo necessário

        // ACT
        emailService.enviarEmailLogin(DESTINATARIO, NOME_USUARIO);

        // VERIFY 1: Garante que o método send foi chamado exatamente 1 vez
        verify(mailSender, times(1)).send(mensagemCaptor.capture());

        // ASSERT: Valida o conteúdo da mensagem capturada
        SimpleMailMessage mensagemEnviada = mensagemCaptor.getValue();

        assertArrayEquals(new String[]{DESTINATARIO}, mensagemEnviada.getTo(), "O destinatário deve ser o correto.");
        assertEquals("Login realizado com sucesso!", mensagemEnviada.getSubject(), "O assunto deve ser o esperado.");
        assertEquals(EMAIL_FROM, mensagemEnviada.getFrom(), "O remetente (FROM) deve ser o configurado.");
        assertTrue(mensagemEnviada.getText().contains("Olá, Fulaninho de Tal!"), "O corpo deve conter o nome do usuário.");
        assertTrue(mensagemEnviada.getText().contains("Seu login foi realizado com sucesso"), "O corpo deve ter o texto de login.");
    }

    // ====================================================================
    // TESTE DO MÉTODO: enviarEmailCadastro
    // ====================================================================
    @Test
    void deveConstruirEEnviarEmailCadastroCorretamente() {
        // ACT
        emailService.enviarEmailCadastro(DESTINATARIO, NOME_USUARIO);

        // VERIFY 1: Garante que o método send foi chamado exatamente 1 vez
        verify(mailSender, times(1)).send(mensagemCaptor.capture());

        // ASSERT: Valida o conteúdo da mensagem capturada
        SimpleMailMessage mensagemEnviada = mensagemCaptor.getValue();

        assertArrayEquals(new String[]{DESTINATARIO}, mensagemEnviada.getTo(), "O destinatário deve ser o correto.");
        assertEquals("Cadastro realizado com sucesso!", mensagemEnviada.getSubject(), "O assunto deve ser o esperado.");
        assertEquals(EMAIL_FROM, mensagemEnviada.getFrom(), "O remetente (FROM) deve ser o configurado.");
        assertTrue(mensagemEnviada.getText().contains("Olá, Fulaninho de Tal!"), "O corpo deve conter o nome do usuário.");
        assertTrue(mensagemEnviada.getText().contains("Seu cadastro foi realizado com sucesso"), "O corpo deve ter o texto de cadastro.");
    }

    // ====================================================================
    // TESTE DO MÉTODO: enviarEmailTrocaSenha
    // ====================================================================
    @Test
    void deveConstruirEEnviarEmailTrocaSenhaCorretamente() {
        // ACT
        emailService.enviarEmailTrocaSenha(DESTINATARIO, NOME_USUARIO);

        // VERIFY 1: Garante que o método send foi chamado exatamente 1 vez
        verify(mailSender, times(1)).send(mensagemCaptor.capture());

        // ASSERT: Valida o conteúdo da mensagem capturada
        SimpleMailMessage mensagemEnviada = mensagemCaptor.getValue();

        assertArrayEquals(new String[]{DESTINATARIO}, mensagemEnviada.getTo(), "O destinatário deve ser o correto.");
        assertEquals("Senha atualizada com sucesso!", mensagemEnviada.getSubject(), "O assunto deve ser o esperado.");
        assertEquals(EMAIL_FROM, mensagemEnviada.getFrom(), "O remetente (FROM) deve ser o configurado.");
        assertTrue(mensagemEnviada.getText().contains("Olá, Fulaninho de Tal!"), "O corpo deve conter o nome do usuário.");
        assertTrue(mensagemEnviada.getText().contains("Sua senha foi alterada com sucesso!"), "O corpo deve ter o texto de troca de senha.");
    }

}