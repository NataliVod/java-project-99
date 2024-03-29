package hexlet.code.service;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {

    private UserRepository userRepository;
    private UserUtils userUtils;

    @Override
    public void createUser(UserDetails userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }


    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public boolean isCurrentUser(Long userId) {
        User currentUser = userUtils.getCurrentUser();
        return currentUser.getId().equals(userId);
    }
}
