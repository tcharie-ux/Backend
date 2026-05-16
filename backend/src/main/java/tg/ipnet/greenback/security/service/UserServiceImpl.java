package tg.ipnet.greenback.security.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.security.UserDetailsImpl;
import tg.ipnet.greenback.security.UserService;
import tg.ipnet.greenback.security.dto.HistoryReponse;
import tg.ipnet.greenback.security.dto.LoginDTO;
import tg.ipnet.greenback.security.dto.PasswordDTO;
import tg.ipnet.greenback.security.dto.ResendVerificationCodeDTO;
import tg.ipnet.greenback.security.dto.RoleDTO;
import tg.ipnet.greenback.security.dto.UserDTO;
import tg.ipnet.greenback.security.dto.UserRoleReponse;
import tg.ipnet.greenback.security.dto.VerifyCodeDTO;
import tg.ipnet.greenback.security.jwt.JwtUtils;
import tg.ipnet.greenback.security.mappers.UserMapper;
import tg.ipnet.greenback.security.model.EmailVerification;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.EmailVerificationRepository;
import tg.ipnet.greenback.security.repository.HistoryRepository;
import tg.ipnet.greenback.security.repository.RoleRepository;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.service.EmailService;
import tg.ipnet.greenback.service.NotificationService;
import tg.ipnet.greenback.utils.ResourceNotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final HistoryRepository historyRepository;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            UserRepository userRepository,
            UserMapper userMapper,
            HistoryRepository historyRepository,
            RoleRepository roleRepository,
            NotificationService notificationService,
            EmailVerificationRepository emailVerificationRepository,
            EmailService emailService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.historyRepository = historyRepository;
        this.roleRepository = roleRepository;
        this.notificationService = notificationService;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(normalizeEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    @Override
    public AuthenticationResponse authenticate(LoginDTO loginDTO) {
        try {
            String username = normalizeEmail(loginDTO.getUsername());
            User utilisateur = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
            if (!utilisateur.isEnable()) {
                throw new BadCredentialsException("Votre compte n'est pas encore verifie. Saisissez le code recu par email.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword())
            );

            utilisateur = notificationService.synchroniserInvitationsArchitecte(utilisateur, null);

            UserDetailsImpl userDetails = UserDetailsImpl.build(utilisateur);
            Authentication authenticationFinale = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    authentication.getCredentials(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationFinale);
            String token = jwtUtils.generateJwtToken(authenticationFinale);

            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            createHistory(userDetails.getId());

            return new AuthenticationResponse(
                    token,
                    userDetails.getId(),
                    userDetails.getFullName(),
                    userDetails.getUsername(),
                    userDetails.getMinistere(),
                    userDetails.getDirection(),
                    roles
            );
        } catch (BadCredentialsException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("pas encore verifie")) {
                throw ex;
            }
            throw new IllegalArgumentException("Les parametres de connexion sont incorrects");
        }
    }

    @Override
    public UserDTO registerClient(UserDTO userDTO) {
        userDTO.setRoles(resolveRegistrationRole(userDTO));
        userDTO.setEnable(false);
        UserDTO savedUser = createUser(userDTO, "Inscription de l'utilisateur ");
        sendVerificationCode(savedUser.getUsername(), EmailVerification.Purpose.REGISTER_CLIENT);
        return savedUser;
    }

    @Override
    public UserDTO registerArchitect(UserDTO userDTO) {
        if (userDTO.getInvitationToken() == null || userDTO.getInvitationToken().isBlank()) {
            throw new IllegalArgumentException("Le token d'invitation est obligatoire");
        }
        NotificationDto invitation = notificationService.consulterInvitation(userDTO.getInvitationToken());
        if (invitation.getEmailDestinataire() != null
                && !invitation.getEmailDestinataire().equalsIgnoreCase(userDTO.getUsername())) {
            throw new IllegalArgumentException("Le compte architecte doit utiliser l'email invite");
        }

        notificationService.accepterInvitationParToken(userDTO.getInvitationToken());
        userDTO.setRoles(Role.ARCHITECTE);
        userDTO.setEnable(false);
        UserDTO savedUser = createUser(userDTO, "Inscription de l'architecte ");
        sendVerificationCode(savedUser.getUsername(), EmailVerification.Purpose.REGISTER_ARCHITECTE);
        return savedUser;
    }

    @Override
    public void verifyCode(VerifyCodeDTO verifyCodeDTO) {
        String email = normalizeEmail(verifyCodeDTO.getEmail());
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailIgnoreCaseAndCodeAndUsedFalseOrderByCreatedAtDesc(email, verifyCodeDTO.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Code de verification invalide"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Code de verification expire");
        }

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setEnable(true);
        userRepository.save(user);

        verification.setUsed(true);
        emailVerificationRepository.save(verification);
    }

    @Override
    public void resendVerificationCode(ResendVerificationCodeDTO resendVerificationCodeDTO) {
        String email = normalizeEmail(resendVerificationCodeDTO.getEmail());
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (user.isEnable()) {
            throw new IllegalArgumentException("Ce compte est deja verifie");
        }

        EmailVerification.Purpose purpose = Role.ARCHITECTE.equals(user.getRoles())
                ? EmailVerification.Purpose.REGISTER_ARCHITECTE
                : EmailVerification.Purpose.REGISTER_CLIENT;
        sendVerificationCode(email, purpose);
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        if (userDTO.getRoles() == null) {
            throw new IllegalArgumentException("Le role est obligatoire");
        }
        return createUser(userDTO, "Enregistrement de l'utilisateur ");
    }

    @Override
    public List<UserRoleReponse> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(userMapper::mapToUserRoleDTO)
                .toList();
    }

    @Override
    public UserRoleReponse getUserById(UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with given id : " + id));

        return userMapper.mapToUserRoleDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id:" + id));

        user.setCodeDirection(userDTO.getDirection());
        user.setCodeMinistere(userDTO.getMinistere() == null ? 0 : userDTO.getMinistere().intValue());
        user.setNom(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setRoles(userDTO.getRoles());
        user.setEnable(userDTO.isEnable());

        History history = new History();
        history.setName("Modification de l'utilisateur " + userDTO.getFullName());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        User updatedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public void deleteUserById(UUID id) {
        Optional<User> optionalUser = userRepository.findByPublicId(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable!");
        }

        User user = optionalUser.get();
        user.setEnable(false);
        userRepository.save(user);
    }

    @Override
    public UserDTO updatePassword(UUID id, PasswordDTO passwordDTO) {
        Optional<User> optionalUser = userRepository.findByPublicId(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }

        User user = optionalUser.get();
        ensureCurrentUserCanManage(user);
        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Le mot de passe actuel ne correspond pas !");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        User updatedPassword = userRepository.save(user);

        History history = new History();
        history.setName("Modification du mot de passe de l'utilisateur " + user.getUsername());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(updatedPassword);
    }

    @Override
    public List<HistoryReponse> getAllHistory() {
        return historyRepository.findAllByOrderByDateHistoryDesc()
                .stream()
                .map(userMapper::mapToHistoryReponse)
                .toList();
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(userMapper::mapToRoleDTO)
                .toList();
    }

    private void checkIfUserExists(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new AlreadyExistException("Ce nom existe deja");
        }
    }

    private UserDTO createUser(UserDTO userDTO, String historyPrefix) {
        userDTO.setUsername(normalizeEmail(userDTO.getUsername()));
        checkIfUserExists(userDTO);
        User user = userMapper.mapToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        savedUser = notificationService.synchroniserInvitationsArchitecte(savedUser, userDTO.getInvitationToken());

        History history = new History();
        history.setName(historyPrefix + userDTO.getFullName());
        history.setUser(savedUser);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(savedUser);
    }

    private void sendVerificationCode(String email, EmailVerification.Purpose purpose) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        EmailVerification verification = new EmailVerification();
        verification.setEmail(normalizeEmail(email));
        verification.setCode(code);
        verification.setPurpose(purpose);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verification.setUsed(false);
        emailVerificationRepository.save(verification);
        emailService.sendVerificationCode(email, code);
    }

    private Role resolveRegistrationRole(UserDTO userDTO) {
        if (userDTO.getInvitationToken() != null && !userDTO.getInvitationToken().isBlank()) {
            NotificationDto invitation = notificationService.consulterInvitation(userDTO.getInvitationToken());
            if (!"ACCEPTEE".equals(invitation.getStatut())) {
                throw new IllegalArgumentException("L'invitation architecte doit etre acceptee avant l'inscription");
            }
            if (invitation.getEmailDestinataire() != null
                    && !invitation.getEmailDestinataire().equalsIgnoreCase(userDTO.getUsername())) {
                throw new IllegalArgumentException("Le compte cree doit utiliser l'email invite");
            }
            return Role.ARCHITECTE;
        }

        Optional<User> existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser.isPresent() && Role.ARCHITECTE.equals(existingUser.get().getRoles())) {
            return Role.ARCHITECTE;
        }

        return Role.CLIENT;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private void ensureCurrentUserCanManage(User targetUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Utilisateur non authentifie");
        }

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur connecte introuvable"));

        boolean isAdmin = Role.ADMIN.equals(currentUser.getRoles());
        boolean isSelf = currentUser.getPublicId() != null && currentUser.getPublicId().equals(targetUser.getPublicId());
        if (!isAdmin && !isSelf) {
            throw new BadCredentialsException("Vous ne pouvez pas modifier le mot de passe d'un autre utilisateur");
        }
    }

    private History createHistory(UUID userId) {
        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        History history = new History();
        history.setName("Connexion de l'utilisateur " + user.getUsername());
        history.setUser(user);
        history.setDateHistory(new Date());

        return historyRepository.save(history);
    }
}
