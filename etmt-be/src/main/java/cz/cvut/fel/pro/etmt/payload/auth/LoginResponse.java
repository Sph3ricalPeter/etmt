package cz.cvut.fel.pro.etmt.payload.auth;

import lombok.Data;

@Data
public class LoginResponse {
    
    private final String accessToken;
    private final String tokenType = "Bearer";

}
