package texas.user.spring.gradle.user;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
@Data
public class UserDAO {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonView(View.Summary.class)
    private Long id;

    @JsonView(View.Short.class)
    private String login;
    @JsonView({View.Short.class,View.Greeting.class})
    private String firstname;
    @JsonView({View.Short.class,View.Greeting.class})
    private String lastname;

    private String password;

    @JsonView({View.Msg.class,View.Greeting.class})
    private String msg;

    public UserDAO() {
    }

    public UserDAO(String login, String password, String firstname, String lastname){
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
