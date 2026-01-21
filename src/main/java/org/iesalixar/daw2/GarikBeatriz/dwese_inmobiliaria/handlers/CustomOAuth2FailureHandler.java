package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements
        AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2FailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        logger.error("Error detallado de OAuth2: {}", exception.getMessage());
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        request.getSession().setAttribute("error.message", exception.getMessage());
        response.sendRedirect("/login?error=true");
    }
}
