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
        private final kg.spring.ort.repository.RoleRepository roleRepository;

        @Override
        public List<kg.spring.ort.entity.User> getAllUsers() {
                return userRepository.findAll();
        }

        @Override
        public void lockUser(Long id) {
                kg.spring.ort.entity.User user = userRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("User not found"));
                user.setLocked(true);
                userRepository.save(user);
        }

        @Override
        public void unlockUser(Long id) {
                kg.spring.ort.entity.User user = userRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("User not found"));
                user.setLocked(false);
                userRepository.save(user);
        }

        @Override
        public void assignRole(Long userId, String roleName) {
                kg.spring.ort.entity.User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found"));
                Role role = roleRepository.findByName(roleName)
                                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));

                if (!user.getRoles().contains(role)) {
                        user.getRoles().add(role);
                        userRepository.save(user);
                }
        }

        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                var myUser = userRepository.findByUsernameWithRoles(username).orElseThrow(
                                () -> new NotFoundException("User not found with username: " + username));
                List<String> roles = myUser.getRoles().stream().map(Role::getName).toList();
                List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                return new User(
                                myUser.getUsername(),
                                myUser.getPassword(),
                                myUser.isEnabled(),
                                true,
                                true,
                                !myUser.isLocked(),
                                authorities);
        }
}
