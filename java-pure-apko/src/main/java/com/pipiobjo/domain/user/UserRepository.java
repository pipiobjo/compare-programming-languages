package com.pipiobjo.domain.user;

import java.util.List;

public interface UserRepository {

    String create(NewUser user);
    List<User> getUsers(UserSearchParams searchParams);

    User getUserByLogin(String login);
}
