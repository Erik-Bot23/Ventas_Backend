package com.erikjarquin.ventas.config.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.PermissionEntity;
import com.erikjarquin.ventas.model.entity.UserEntity;

//Mostrar los roles con permisos
@Component
public class SecurityAuthorityMapper {
    public List<GrantedAuthority> mapAuthorities(UserEntity user){
        List<GrantedAuthority> authorities = new ArrayList<>();

        //Rol
        authorities.add(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );

        //Permisos
        for(PermissionEntity permission : user.getRole().getPermissions()){
            authorities.add(
                new SimpleGrantedAuthority(permission.getName().name())
            );
        }

        return authorities;
    }
}
