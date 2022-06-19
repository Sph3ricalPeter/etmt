package cz.cvut.fel.pro.etmt.controller;

import java.net.URI;
import java.util.Objects;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cz.cvut.fel.pro.etmt.model.User;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.BooleanPayload;
import cz.cvut.fel.pro.etmt.payload.auth.LoginResponse;
import cz.cvut.fel.pro.etmt.payload.auth.UserCredentialsPayload;
import cz.cvut.fel.pro.etmt.payload.user.UserInfoPayload;
import cz.cvut.fel.pro.etmt.security.CurrentUser;
import cz.cvut.fel.pro.etmt.security.CustomUserDetails;
import cz.cvut.fel.pro.etmt.service.AuthService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Validated @RequestBody UserCredentialsPayload loginRequest) {
        log.debug("authenticateUser() - loginRequest: {}", loginRequest);

        var jwt = authService.authenticateUserAndGetToken(
                loginRequest.getUsername(),
                loginRequest.getPassword());

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Validated @RequestBody UserCredentialsPayload signUpRequest) {
        log.debug("registerUser() - signUpRequest: {}", signUpRequest);

        var user = authService.registerUser(signUpRequest.getUsername(), signUpRequest.getPassword());

        if (Objects.isNull(user)) {
            return ResponseEntity.badRequest().body(new ApiResponse("Username is already taken!"));
        }

        var location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse("User registered successfully"));
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<BooleanPayload> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        log.debug("checkUsernameAvailability() - username: {}", username);

        return ResponseEntity.ok().body(new BooleanPayload(authService.isUsernameAvailable(username)));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<UserInfoPayload> getCurrentUser(@CurrentUser CustomUserDetails currentUserDetails) {
        log.debug("getCurrentUser() - currentUserDetails: {}", currentUserDetails);

        return ResponseEntity.ok().body(
                new UserInfoPayload(currentUserDetails.getUsername()));
    }

}
