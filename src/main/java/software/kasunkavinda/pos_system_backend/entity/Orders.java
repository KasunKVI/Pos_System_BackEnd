package software.kasunkavinda.pos_system_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Orders {

    @Id
    private String id;
    private Date date;
    private float balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToMany( fetch = FetchType.LAZY)
    private List<Item> items;

}
