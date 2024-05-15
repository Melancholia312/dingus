package com.melancholia.educationplatform.user.permissions;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }

        String permissionString = PrivilegeService.constructPermissionByType(
                targetDomainObject.getClass().getSimpleName(),
                permission.toString());

        return hasPrivilege(auth, permissionString);
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || (targetId == null) || !(permission instanceof String)) {
            return false;
        }

        String permissionStringWithId = PrivilegeService.constructPermissionById(
                targetType,
                targetId.toString(),
                permission.toString());
        String permissionStringWithType = PrivilegeService.constructPermissionByType(
                targetType,
                permission.toString());

        return hasPrivilege(auth, permissionStringWithId) || hasPrivilege(auth, permissionStringWithType);
    }

    private boolean hasPrivilege(Authentication auth, String permissionString) {
        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
            if (grantedAuth.getAuthority().contains(permissionString)) {
                return true;
            }
        }
        return false;
    }
}