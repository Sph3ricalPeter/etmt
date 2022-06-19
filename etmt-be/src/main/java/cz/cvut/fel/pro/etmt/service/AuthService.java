package cz.cvut.fel.pro.etmt.service;

import java.util.Set;

import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cz.cvut.fel.pro.etmt.model.Role;
import cz.cvut.fel.pro.etmt.model.User;
import cz.cvut.fel.pro.etmt.repository.UserRepository;
import cz.cvut.fel.pro.etmt.security.JwtTokenProvider;

@Service
@AllArgsConstructor
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    private ItemService itemService;

    public String authenticateUserAndGetToken(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.generateToken(authentication);
    }

    public User registerUser(String username, String password) {
        if (!isUsernameAvailable(username)) {
            return null;
        }

        // Creating user's account
        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .rootCategoryId(itemService.createRootCategory())
                .role(Set.of(Role.ROLE_USER))
                .build();

        // Update user reference the the one saved in DB
        user = userRepository.save(user);

        return user;
    }

    public boolean isUsernameAvailable(String username) {
        var optionalUser = userRepository.getByUsername(username);
        return optionalUser.isEmpty();
    }

    public User getCurrentUser() {
        var userDetail = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var optionalUser = userRepository.getById(userDetail.getId());
        if (optionalUser.isEmpty()) {
            throw new AuthenticationServiceException("no user is currently logged in");
        }
        return optionalUser.get();
    }

}
