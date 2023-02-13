package com.pipiobjo.domain.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewUser {

    String login;
    String firstName;
    String lastName;
    String password;
}
