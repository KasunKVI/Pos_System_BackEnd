package software.kasunkavinda.pos_system_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class Item {

    @Id
    private String id;
    private int qty;
    private String name;
    private Date exp;
    private String price;
    
    @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
    private List<Orders> orders;

}
