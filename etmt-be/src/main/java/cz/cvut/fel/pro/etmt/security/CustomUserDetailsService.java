package cz.cvut.fel.pro.etmt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cz.cvut.fel.pro.etmt.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadById(String id) throws Exception {
        var user = userRepository.getById(id)
                .orElseThrow(() -> new Exception(String.format("user with id %d doesn't exist", id)));
        return CustomUserDetails.create(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("user with username %s doesn't exist", username)));
        return CustomUserDetails.create(user);
    }

}
