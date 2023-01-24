package texas.user.spring.gradle.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


import static org.hibernate.cache.spi.support.SimpleTimestamper.timeOut;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user")
    @JsonView(View.Short.class)
    public List<UserDAO> getAll(@RequestParam(required = false) String login) {
        Spliterator<UserDAO> users;
        if (login != null){
            users = repository.findByLogin(login).spliterator();
        } else {
            users = repository.findAll().spliterator();
        }

        timeOut();

        return StreamSupport
                .stream(users, false)
                .collect(Collectors.toList());
    }


    @PostMapping("/user")
    public ResponseEntity post(@RequestBody(required = false) UserDAO user) {

        verifyCorrectPayload(user);

        final MappingJacksonValue result = new MappingJacksonValue(user);
        ObjectMapper mapper = new ObjectMapper();

        timeOut();

        if (!verifyUserExists(user.getLogin())) {
            try {
                mapper.writeValueAsString(repository.save(user));
                addNewUserAuth(user);
                result.setSerializationView(View.Summary.class);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (Exception e) {}
        } else {
            user.setMsg("user already exists");
            result.setSerializationView(View.Msg.class);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/greeting")
    @ResponseBody
    public ResponseEntity greeting() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        repository.findByLogin(userDetails.getUsername()).get(0).setMsg("Hello " + repository.findByLogin(userDetails.getUsername()).get(0).getFirstname() + " " + repository.findByLogin(userDetails.getUsername()).get(0).getLastname() + "!");

        final MappingJacksonValue result = new MappingJacksonValue(repository.findByLogin(userDetails.getUsername()).get(0));

        result.setSerializationView(View.Greeting.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    private boolean verifyUserExists(String login) {
        return repository.findByLogin(login).size() >=1;
    }

    private void verifyCorrectPayload(UserDAO user) {
//        TODO: add more checks
    }

    private void addNewUserAuth(UserDAO userDAO){
        UserDetails user = User
                .withUsername(userDAO.getLogin())
                .password(passwordEncoder.encode(userDAO.getPassword()))
                .roles("USER_ROLE")
                .build();
        inMemoryUserDetailsManager.createUser(user);
    }
}