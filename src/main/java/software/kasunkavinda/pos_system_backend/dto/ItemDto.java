package software.kasunkavinda.pos_system_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDto {

    private String id;
    private int qty;
    private String name;
    private Date exp;
    private String price;
}
