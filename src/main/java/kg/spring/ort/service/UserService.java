package kg.spring.ort.service;

import kg.spring.ort.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getAllUsers();

    void lockUser(Long id);

    void unlockUser(Long id);

    void assignRole(Long userId, String roleName);
}
