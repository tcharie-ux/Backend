package tg.ipnet.greenback.security.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import tg.ipnet.greenback.security.UserService;
import tg.ipnet.greenback.dto.NotificationCreationDTO;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.security.dto.*;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.security.service.AuthenticationResponse;
import tg.ipnet.greenback.service.NotificationService;
import tg.ipnet.greenback.utils.ResourceNotFoundException;


import java.util.List;
import java.util.UUID;

@Tag(name = "Gestion des utilisateurs", description = "Point d'entrée des utilisateurs")
@RestController
@RequestMapping("/api/v1")
public class UserApi {

    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public UserApi(UserService userService, UserRepository userRepository, NotificationService notificationService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/login")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Authenticate",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé/invalide",
                            responseCode = "401"
                    )
                    ,
                    @ApiResponse(
                            description = "Point d'entré non trouvé",
                            responseCode = "404"
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid LoginDTO loginDTO) {
        return  ResponseEntity.ok(userService.authenticate(loginDTO));
    }

    @PostMapping("/register")
    @Operation(
            description = "Inscription publique d'un client. Aucun JWT requis.",
            summary = "Register client account",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Conflit sur le nom d'utilisateur",
                            responseCode = "409"
                    )
            }
    )
    public ResponseEntity<UserDTO> registerClient(@RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(userService.registerClient(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/register-architect")
    @Operation(description = "Inscription architecte depuis un token d'invitation.", summary = "Register architect account")
    public ResponseEntity<UserDTO> registerArchitect(@RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(userService.registerArchitect(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/verify-code")
    @Operation(description = "Valide le code recu par email et active le compte.", summary = "Verify email code")
    public ResponseEntity<Void> verifyCode(@RequestBody @Valid VerifyCodeDTO verifyCodeDTO) {
        userService.verifyCode(verifyCodeDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification-code")
    @Operation(description = "Renvoie un nouveau code de verification email.", summary = "Resend email code")
    public ResponseEntity<Void> resendVerificationCode(@RequestBody @Valid ResendVerificationCodeDTO resendVerificationCodeDTO) {
        userService.resendVerificationCode(resendVerificationCodeDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invite-architect")
    @Operation(description = "Invite un architecte par email pour un projet client.", summary = "Invite architect")
    public ResponseEntity<NotificationDto> inviteArchitect(@RequestBody @Valid InviteArchitectDTO inviteArchitectDTO) {
        NotificationCreationDTO demande = new NotificationCreationDTO();
        demande.setEmailDestinataire(inviteArchitectDTO.getEmailArchitecte());
        demande.setIdProjet(inviteArchitectDTO.getIdProjet());
        demande.setTypeNotification("INVITATION_ARCHITECTE");
        demande.setMessage(inviteArchitectDTO.getMessage() == null || inviteArchitectDTO.getMessage().isBlank()
                ? "Invitation architecte"
                : inviteArchitectDTO.getMessage());
        return new ResponseEntity<>(notificationService.envoyerNotification(demande), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','ARCHITECTE','CLIENT')")
    @Operation(description = "Recupere le profil de l'utilisateur connecte.", summary = "Current user profile")
    public ResponseEntity<UserRoleReponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Utilisateur connecte introuvable");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur connecte introuvable"));

        return ResponseEntity.ok(new UserRoleReponse(
                user.getId(),
                user.getNom(),
                user.getUsername(),
                user.getCreatedAt(),
                user.isEnable(),
                (long) user.getCodeMinistere(),
                null,
                user.getCodeDirection(),
                null,
                user.getRoles()
        ));
    }

    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Récupère la liste de tous les rôles disponibles. Nécessite un JWT ADMIN.",
            summary = "Liste des roles",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<RoleDTO>> getAllRole() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Enregistre un nouvel utilisateur. Nécessite un JWT ADMIN.",
            summary = "Save new user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> saveUsers(@RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Liste tous les utilisateurs. Nécessite un JWT ADMIN.",
            summary = "Retrieve all user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<UserRoleReponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ARCHITECTE')")
    @Operation(
            description = "Récupère un utilisateur par son UUID. Nécessite un JWT ADMIN ou DISPENSATEUR.",
            summary = "Retrieve a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserRoleReponse> getUserById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Met à jour un utilisateur. Nécessite un JWT ADMIN.",
            summary = "Update a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> updateUsers(@RequestBody @Valid UserDTO userDTO,@PathVariable("id")  UUID id){
        return ResponseEntity.ok(userService.updateUser(userDTO,id));
    }

    @PutMapping("/users/change_password/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ARCHITECTE','CLIENT')")
    @Operation(
            description = "Change le mot de passe de l'utilisateur. Nécessite un JWT valide.",
            summary = "Change password a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> updatePassword(@PathVariable("id") UUID id, @RequestBody @Valid PasswordDTO passwordDTO){
        return ResponseEntity.ok(userService.updatePassword(id,passwordDTO));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Récupère l'historique des connexions et actions. Nécessite un JWT ADMIN.",
            summary = "History  all",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<HistoryReponse>> getAllHistory() {
        return ResponseEntity.ok(userService.getAllHistory());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Désactive un utilisateur. Nécessite un JWT ADMIN.",
            summary = "Delete a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Void>  deleteUserById(@PathVariable("id") UUID id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(204).build();
    }


}
