package cz.cvut.fel.pro.etmt.payload.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfoPayload {

    private String id;
    private String title;

}
