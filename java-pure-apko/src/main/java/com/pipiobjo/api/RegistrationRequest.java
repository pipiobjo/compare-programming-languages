package com.pipiobjo.api;

import lombok.Value;

@Value
class RegistrationRequest {

    String login;
    String password;
    String firstname;
    String lastname;
}
