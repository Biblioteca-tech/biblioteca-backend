package biblioteca.onliine.biblioteca.usecase.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailLogin(String destinatario, String nomeUsuario) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("Login realizado com sucesso ✅");
        mensagem.setText("Olá, " + nomeUsuario + "!\n\nSeu login foi realizado com sucesso em nossa plataforma.\n\nSe não foi você, entre em contato imediatamente.");
        mensagem.setFrom("bibliotecaMedic@gmail.com");

        mailSender.send(mensagem);
    }
    public void enviarEmailCadastro(String destinatario, String nomeUsuario) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("Cadastro realizado com sucesso 🎉");
        mensagem.setText("Olá, " + nomeUsuario + "!\n\nSeu cadastro foi realizado com sucesso em nossa plataforma!! Leia bastante livros e se divirta ao máximo.\n\nSe não foi você, entre em contato imediatamente.");
        mensagem.setFrom("bibliotecaMedic@gmail.com");

        mailSender.send(mensagem);
    }

}
