package tg.ipnet.greenback.security.mappers;


import org.springframework.stereotype.Service;
import tg.ipnet.greenback.security.dto.HistoryReponse;
import tg.ipnet.greenback.security.dto.RoleDTO;
import tg.ipnet.greenback.security.dto.UserDTO;
import tg.ipnet.greenback.security.dto.UserRoleReponse;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.Role;
import tg.ipnet.greenback.security.model.User;

@Service
public class UserMapper {

    public User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setNom(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEnable(userDTO.isEnable());
        user.setCodeMinistere(userDTO.getMinistere() == null ? 0 : userDTO.getMinistere().intValue());
        user.setCodeDirection(userDTO.getDirection());
        user.setRoles(userDTO.getRoles());
        return user;
    }

    public  UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(user.getNom());
        userDTO.setUsername(user.getUsername());
        userDTO.setEnable(user.isEnable());
        userDTO.setRoles(user.getRoles());
        userDTO.setMinistere((long) user.getCodeMinistere());
        userDTO.setDirection(user.getCodeDirection());
        userDTO.setPublicId(user.getPublicId());

        return  userDTO;
    }
    public UserRoleReponse mapToUserRoleDTO(User user) {
        UserRoleReponse userRoleReponse = new UserRoleReponse();
        userRoleReponse.setId(user.getId());
        userRoleReponse.setFullName(user.getNom());
        userRoleReponse.setUsername(user.getUsername());
        userRoleReponse.setCreatedAt(user.getCreatedAt());
        userRoleReponse.setEnable(user.isEnable());

            userRoleReponse.setMinistere((long) user.getCodeMinistere());
            userRoleReponse.setDirection( user.getCodeDirection());


        userRoleReponse.setRoles(user.getRoles());

        return userRoleReponse;
    }

    public RoleDTO mapToRoleDTO(Role role) {

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName().name());

        return roleDTO;
    }
    public HistoryReponse mapToHistoryReponse(History history) {

        HistoryReponse historyReponse = new HistoryReponse();
        historyReponse.setId(history.getId());
        historyReponse.setFullName(history.getUser().getNom());
        historyReponse.setName(history.getName());
        historyReponse.setDateHistory(history.getDateHistory());

        return historyReponse;
    }
}
