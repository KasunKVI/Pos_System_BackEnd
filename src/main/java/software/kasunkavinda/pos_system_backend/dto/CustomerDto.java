package software.kasunkavinda.pos_system_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerDto implements Serializable {

    private String id;
    private String name;
    private int mobile_no;
    private Date dob;
    private String email;
    private String gender;
    private String address;
}
