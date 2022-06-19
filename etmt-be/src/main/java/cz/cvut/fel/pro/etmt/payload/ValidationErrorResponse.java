package cz.cvut.fel.pro.etmt.payload;

import lombok.Data;

import java.util.List;

@Data
public class ValidationErrorResponse {

    private final List<String> errors;

}
