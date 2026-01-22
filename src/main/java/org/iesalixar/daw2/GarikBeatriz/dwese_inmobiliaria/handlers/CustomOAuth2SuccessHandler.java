package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.UserRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // Obtiene "gitlab", "microsoft", "google" etc.
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String username = null;

        // Lógica específica según el proveedor
        if ("gitlab".equals(provider)) {
            username = oAuth2User.getAttribute("nickname");
            if (username == null) {
                username = oAuth2User.getAttribute("preferred_username");
            }
        } else if ("azure".equals(provider) || "microsoft".equals(provider)) {
            username = oAuth2User.getAttribute("preferred_username");
            if (username == null) {
                username = oAuth2User.getAttribute("email");
            }
        } else {
            // Caso por defecto o para otros proveedores
            username = oAuth2User.getAttribute("login");
        }

        // Validación común
        if (username == null || !userRepository.existsByUsername(username)) {
            throw new OAuth2AuthenticationException("El usuario '" + (username != null ? username : "desconocido") +
                    "' del proveedor '" + provider + "' no está registrado en la base de datos local.");
        }

        // Convertir a autenticación local
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        response.sendRedirect("/");
    }
}