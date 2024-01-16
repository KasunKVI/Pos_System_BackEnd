package software.kasunkavinda.pos_system_backend.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.sql.Date;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Customer {

    @Id
    private String nic;
    private String name;
    private int mobile_no;
    private Date dob;
    private String email;
    private String gender;
    private String address;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Orders> orders;

}
