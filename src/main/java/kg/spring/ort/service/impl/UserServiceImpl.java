package kg.spring.ort.service.impl;

import kg.spring.ort.entity.Role;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var myUser = userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User not found with username: " + username)
        );
        List<String> roles = myUser.getRoles().stream().map(Role::getName).toList();
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(
                myUser.getUsername(),
                myUser.getPassword(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}
