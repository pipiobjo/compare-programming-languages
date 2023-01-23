package com.pipiobjo.domain.greeting;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GreetingMessage {
    String msg;
    String firstname;
    String lastname;

}
