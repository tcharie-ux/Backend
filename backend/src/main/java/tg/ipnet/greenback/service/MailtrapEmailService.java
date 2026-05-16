package tg.ipnet.greenback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailtrapEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(MailtrapEmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public MailtrapEmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        String text = "Bonjour cher client nous sommes heureux de vous accueillir, nous esperons que vous apprécierez notre plateforme,\n\nVotre code de verification Archimorph est : " + code
                + "\n\nCe code expire dans 15 minutes.";
        send(email, "Code de verification Archimorph", text);
    }

    @Override
    public void sendArchitectInvitation(String email, String invitationLink, String projectName, String message) {
        StringBuilder text = new StringBuilder();
        text.append("Bonjour cher client nous sommes heureux de vous accueillir, nous esperons que vous apprécierez notre plateforme,\n\nVous avez recu une invitation pour rejoindre le projet ")
                .append(projectName == null ? "Archimorph" : projectName)
                .append(".\n\nLien d'inscription : ")
                .append(invitationLink);
        if (message != null && !message.isBlank()) {
            text.append("\n\nMessage du client :\n").append(message);
        }
        send(email, "Invitation architecte Archimorph", text.toString());
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException ex) {
            logger.warn("Email non envoye a {}. Verifiez la configuration Mailtrap. Detail: {}", to, ex.getMessage());
        }
    }
}
