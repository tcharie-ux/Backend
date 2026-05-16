package tg.ipnet.greenback.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import tg.ipnet.greenback.security.dto.*;
import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    tg.ipnet.greenback.security.service.AuthenticationResponse authenticate(LoginDTO loginDTO);
    UserDTO registerClient(UserDTO userDTO);
    UserDTO registerArchitect(UserDTO userDTO);
    void verifyCode(VerifyCodeDTO verifyCodeDTO);
    void resendVerificationCode(ResendVerificationCodeDTO resendVerificationCodeDTO);
    UserDTO saveUser(UserDTO userDTO);
    List<UserRoleReponse> getAllUsers();
    UserRoleReponse getUserById(UUID id);
    UserDTO updateUser(UserDTO userDTO, UUID id);
    void deleteUserById(UUID id);
    UserDTO updatePassword(UUID id, PasswordDTO passwordDTO);
    List<HistoryReponse> getAllHistory();
    List<RoleDTO> getAllRoles();
}
