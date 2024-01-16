package software.kasunkavinda.pos_system_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDto implements Serializable {

    private String id;
    private Date date;
    private float balance;
    private String customer_id;
    private List<OrderItem> items;


}
