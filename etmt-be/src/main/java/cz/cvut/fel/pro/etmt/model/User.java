package cz.cvut.fel.pro.etmt.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Document
@Builder
@Getter
@Setter
public class User {

    @Id
    private String id;
    
    private String username;
    private String password;
    private Set<Role> role;

    private String rootCategoryId;

}
