package tg.ipnet.greenback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tg.ipnet.greenback.entity.Utilisateur;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.repository.UtilisateurRepository;
import tg.ipnet.greenback.security.dto.LoginDTO;
import tg.ipnet.greenback.security.jwt.JwtUtils;
import tg.ipnet.greenback.security.service.AuthenticationResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Authentification", description = "Gestion de la connexion et de l'inscription")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/health")
    @Operation(summary = "Etat du backend", description = "Vérifie que l'API est accessible sans authentification")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Backend authentication API is running"
        ));
    }

    @PostMapping("/auth/login")
    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Authentifie un `Utilisateur` via son email ou son nom puis retourne un JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentification réussie"),
                    @ApiResponse(responseCode = "401", description = "Identifiants invalides")
            }
    )
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginDTO loginDTO) {
        String identifiant = loginDTO.getUsername();
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(identifiant)
                .or(() -> utilisateurRepository.findByNomIgnoreCase(identifiant))
                .orElseThrow(() -> new BadCredentialsException("Email/nom ou mot de passe invalide"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), utilisateur.getPassword())) {
            throw new BadCredentialsException("Email/nom ou mot de passe invalide");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                utilisateur.getEmail(),
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String fullName = ((utilisateur.getPrenom() == null ? "" : utilisateur.getPrenom() + " ")
                + (utilisateur.getNom() == null ? "" : utilisateur.getNom())).trim();

        return ResponseEntity.ok(new AuthenticationResponse(
                jwtUtils.generateJwtToken(authentication),
                null,
                fullName,
                utilisateur.getEmail(),
                null,
                null,
                authorities.stream().map(SimpleGrantedAuthority::getAuthority).toList()
        ));
    }

    @PostMapping("/auth/register")
    @Operation(
            summary = "Inscription d'un client",
            description = "Crée un compte basé sur l'entité `Utilisateur` sans JWT",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Compte créé"),
                    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
            }
    )
    public ResponseEntity<Map<String, Object>> register(@RequestBody Utilisateur utilisateur) {
        if (utilisateur.getEmail() == null || utilisateur.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        if (utilisateurRepository.existsByEmailIgnoreCase(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Cet email existe deja");
        }

        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.CLIENT);
        }
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", savedUtilisateur.getId());
        response.put("nom", savedUtilisateur.getNom());
        response.put("prenom", savedUtilisateur.getPrenom());
        response.put("email", savedUtilisateur.getEmail());
        response.put("role", savedUtilisateur.getRole());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
