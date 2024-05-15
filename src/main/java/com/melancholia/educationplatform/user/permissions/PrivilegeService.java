package com.melancholia.educationplatform.user.permissions;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    private final UserRepository userRepository;

    public void addPermissionToUser(Authentication authentication, String objectType, String permission){
        addPermissionToUser(authentication, objectType, null, permission);
    }

    @Transactional
    public void addPermissionToUser(Authentication authentication, String objectType, String idOfTargetObject, String permission){
        String stringPermission;

        if (idOfTargetObject == null) {
            stringPermission = constructPermissionByType(objectType, permission);
        } else {
            stringPermission = constructPermissionById(objectType, idOfTargetObject, permission);
        }
        for (GrantedAuthority grantedAuth : authentication.getAuthorities()) {
            if (grantedAuth.getAuthority().contains(stringPermission)) {
                return;
            }
        }

        Privilege privilege = privilegeRepository.findByName(stringPermission);
        if (privilege == null) {
            privilege = new Privilege(stringPermission);
            privilegeRepository.save(privilege);
        }

        User user = (User) authentication.getPrincipal();
        user.addPrivilege(privilege);
        userRepository.addPermission(user.getId(), privilege.getId());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Transactional
    public void deletePermissionUser(long userId, long courseId){
        userRepository.deletePermission(userId, courseId);

    }

    public static String constructPermissionById(String targetType, String targetId, String permission){
        return String.format("%s_%s_%s",
                targetType.toUpperCase(),
                targetId,
                permission.toUpperCase());
    }

    public static String constructPermissionByType(String targetType, String permission){
        return String.format("%s_%s",
                targetType.toUpperCase(),
                permission.toUpperCase());
    }

}
