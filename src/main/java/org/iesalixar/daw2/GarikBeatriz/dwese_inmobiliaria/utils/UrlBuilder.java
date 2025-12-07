package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component("urlBuilder")
public class UrlBuilder {

    public String replaceParam(String param, String value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return ServletUriComponentsBuilder.fromRequest(request)
                .replaceQueryParam(param, value)
                .build()
                .toUriString();
    }
}