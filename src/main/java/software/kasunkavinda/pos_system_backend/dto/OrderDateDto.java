package software.kasunkavinda.pos_system_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDateDto {

    private String id;
    private Date date;
    private float balance;
    private String customer_id;
    private String name;

}
