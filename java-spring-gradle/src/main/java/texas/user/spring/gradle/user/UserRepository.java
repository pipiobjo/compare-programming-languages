package texas.user.spring.gradle.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserDAO,Long> {
    List<UserDAO> findByLogin(String login);
}
