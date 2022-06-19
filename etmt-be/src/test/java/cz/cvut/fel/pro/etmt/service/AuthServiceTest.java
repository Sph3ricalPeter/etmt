package cz.cvut.fel.pro.etmt.service;

import cz.cvut.fel.pro.etmt.TestConstants;
import cz.cvut.fel.pro.etmt.repository.UserRepository;
import cz.cvut.fel.pro.etmt.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService testSubject;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ItemService itemService;

    @Spy
    private JwtTokenProvider tokenProvider;

    @Test
    public void testIsUsernameAvailable_shouldReturnTrue() {
        when(userRepository.getByUsername(TestConstants.USER_NOT_REGISTERED.getUsername())).thenReturn(Optional.empty());

        var isActuallyAvailable = testSubject.isUsernameAvailable(TestConstants.USER_NOT_REGISTERED.getUsername());

        assertTrue(isActuallyAvailable);
    }

    @Test
    public void testIsUsernameAvailable_shouldReturnFalse() {
        when(userRepository.getByUsername(TestConstants.REGISTERED_USER.getUsername())).thenReturn(Optional.of(TestConstants.REGISTERED_USER));

        var isActuallyAvailable = testSubject.isUsernameAvailable(TestConstants.REGISTERED_USER.getUsername());

        assertFalse(isActuallyAvailable);
    }

    @Test
    public void testRegisterUser_shouldReturnValidUser() {
        when(userRepository.getByUsername(TestConstants.USER_NOT_REGISTERED.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(TestConstants.USER_NOT_REGISTERED);

        var actualUser = testSubject.registerUser(TestConstants.USER_NOT_REGISTERED.getUsername(), TestConstants.USER_NOT_REGISTERED.getPassword());

        assertEquals(TestConstants.USER_NOT_REGISTERED, actualUser);
    }

}
