package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.User;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends OidcUserService { // Cambiado a OidcUserService

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        if (email == null) {
            throw new OAuth2AuthenticationException("No se ha podido obtener el email de GitLab");
        }

        // Persistencia y obtención del rol
        User userInDb = userRepository.findByEmail(email).map(user -> {
            user.setName(name);
            return userRepository.save(user);
        }).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole("ROLE_USER");
            return userRepository.save(newUser);
        });

        // Mapear autoridades: Scopes de GitLab + Rol de nuestra BD
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(userInDb.getRole()));

        // Devolver el usuario OIDC con el rol incluido
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}