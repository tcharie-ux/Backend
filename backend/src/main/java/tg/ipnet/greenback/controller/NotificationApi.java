package tg.ipnet.greenback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tg.ipnet.greenback.dto.NotificationCreationDTO;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.service.NotificationService;

import java.util.List;

@Tag(name = "Notifications", description = "Gestion des notifications entre architecte et client")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationApi {

    private final NotificationService notificationService;

    public NotificationApi(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(
            summary = "Envoyer une notification",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Notification envoyee"),
                    @ApiResponse(responseCode = "401", description = "Acces refuse")
            }
    )
    public ResponseEntity<NotificationDto> envoyerNotification(@RequestBody @Valid NotificationCreationDTO demande) {
        return new ResponseEntity<>(notificationService.envoyerNotification(demande), HttpStatus.CREATED);
    }

    @GetMapping("/received")
    @Operation(summary = "Lister les notifications recues")
    public ResponseEntity<List<NotificationDto>> listerNotificationsRecues() {
        return ResponseEntity.ok(notificationService.listerNotificationsRecues());
    }

    @GetMapping("/sent")
    @Operation(summary = "Lister les notifications envoyees")
    public ResponseEntity<List<NotificationDto>> listerNotificationsEnvoyees() {
        return ResponseEntity.ok(notificationService.listerNotificationsEnvoyees());
    }

    @PutMapping("/{notificationId}/accept")
    @Operation(summary = "Accepter une notification")
    public ResponseEntity<NotificationDto> accepterNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.accepterNotification(notificationId));
    }

    @GetMapping("/invitations/{tokenInvitation}")
    @Operation(summary = "Consulter une invitation architecte par token")
    public ResponseEntity<NotificationDto> consulterInvitation(@PathVariable String tokenInvitation) {
        return ResponseEntity.ok(notificationService.consulterInvitation(tokenInvitation));
    }

    @PutMapping("/invitations/{tokenInvitation}/accept")
    @Operation(summary = "Accepter une invitation architecte depuis un lien email")
    public ResponseEntity<NotificationDto> accepterInvitationParToken(@PathVariable String tokenInvitation) {
        return ResponseEntity.ok(notificationService.accepterInvitationParToken(tokenInvitation));
    }
}
