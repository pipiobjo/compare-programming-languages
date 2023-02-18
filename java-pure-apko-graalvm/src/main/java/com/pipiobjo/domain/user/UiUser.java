package com.pipiobjo.domain.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UiUser {

    String login;
    String firstName;
    String lastName;
}

