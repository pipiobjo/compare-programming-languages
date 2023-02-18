package com.pipiobjo.domain.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {

    String id;
    String login;
    String password;
    String firstName;
    String lastName;
}
