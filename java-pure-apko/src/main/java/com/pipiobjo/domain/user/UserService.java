package com.pipiobjo.domain.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String create(NewUser user) throws UserAlreadyException {
        UserSearchParams searchParams = UserSearchParams.builder().login(user.getLogin()).build();
        List<User> users = userRepository.getUsers(searchParams);
        if(users.size()>0){
            log.info("user with login {} already exists", user.getLogin());
            throw new UserAlreadyException("User login already exists");
        }
        return userRepository.create(user);
    }

    public List<User> getUsers(UserSearchParams searchParams) {
        return userRepository.getUsers(searchParams);
    }

    public boolean login(String username, String password){
        User userByLogin = userRepository.getUserByLogin(username);
        String passwordExpected = userByLogin.getPassword();
        if (passwordExpected.equals(password)){
            return true;
        }else {
            return false;
        }

    }
}
