package tg.ipnet.greenback.service;

public interface EmailService {
    void sendVerificationCode(String email, String code);
    void sendArchitectInvitation(String email, String invitationLink, String projectName, String message);
}
