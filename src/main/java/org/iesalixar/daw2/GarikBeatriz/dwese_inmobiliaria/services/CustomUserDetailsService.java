package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Role;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.User;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return
                org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(Role::getName)
                                .toList()
                                .toArray(new String[0]))
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(!user.isEnabled())
                        .build();
    }
}
