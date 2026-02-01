/*
package uz.zafar.onlineshoptelegrambot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Optional<User> findByLogin(String login) {
        login = login.toLowerCase();
        if (login.contains("@")) {
            return userRepository.findByEmail(login);
        }
        return userRepository.findByUsername(login);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<User> byLogin = findByLogin(username);
        if (byLogin.isEmpty()) {
            throw new UsernameNotFoundException(
                    "User topilmadi: " + username
            );
        }
        uz.zafar.onlineshoptelegrambot.db.entity.user.User user =
                userRepository.findByUsername(byLogin.get().getUsername().toLowerCase())
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User topilmadi: " + username
                                )
                        );

        // ✅ ROLE_ prefiksi MUHIM
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(!user.getEnabled())   // enabled=false bo‘lsa blok
                .disabled(!user.getEnabled())
                .build();
    }
}
*/
package uz.zafar.onlineshoptelegrambot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Optional<User> findByLogin(String login) {
        login = login.toLowerCase();
        if (login.contains("@")) {
            return userRepository.findByEmail(login);
        }
        return userRepository.findByUsername(login);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User topilmadi: " + username));

        return new UserPrincipal(user);
    }
}
