package com.pipiobjo.data.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.pipiobjo.domain.user.NewUser;
import com.pipiobjo.domain.user.User;
import com.pipiobjo.domain.user.UserRepository;
import com.pipiobjo.domain.user.UserSearchParams;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> USERS_STORE = new ConcurrentHashMap();

    @Override
    public String create(NewUser newUser) {
        String id = UUID.randomUUID().toString();
        User user = User.builder()
            .id(id)
            .login(newUser.getLogin())
            .password(newUser.getPassword())
            .firstName(newUser.getFirstName())
            .lastName(newUser.getLastName())
            .build();
        USERS_STORE.put(newUser.getLogin(), user);

        return id;
    }

    @Override
    public List<User> getUsers(UserSearchParams searchParams) {
        if(searchParams.getLogin() != null && !searchParams.getLogin().equals("")){
            List<User> result = new ArrayList<>();
            User user = USERS_STORE.get(searchParams.getLogin());
            if (user != null){
                result.add(user);
            }
            return result;
        }
        List<User> users = USERS_STORE.values().stream().toList();
        return users;
    }

    @Override
    public User getUserByLogin(String login) {
        return USERS_STORE.get(login);
    }
}
