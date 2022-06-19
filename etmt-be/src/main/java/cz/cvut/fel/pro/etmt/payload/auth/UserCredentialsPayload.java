package cz.cvut.fel.pro.etmt.payload.auth;

import lombok.Data;

@Data
public class UserCredentialsPayload {
    
    private final String username;
    private final String password;

}
