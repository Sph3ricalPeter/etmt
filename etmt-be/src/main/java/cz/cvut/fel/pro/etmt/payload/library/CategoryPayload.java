package cz.cvut.fel.pro.etmt.payload.library;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryPayload extends ItemPayload {

    private List<ItemPayload> children;

}
