package et.gov.online_exam.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "users" , schema = "public")
public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usersId;


    private String firstName;
    private String lastName;
    private String address;
    private String username;
    private String emailAddress;
    private String password;
    private Boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user", fetch = FetchType.EAGER)
    private Set<Role> roles;


}
