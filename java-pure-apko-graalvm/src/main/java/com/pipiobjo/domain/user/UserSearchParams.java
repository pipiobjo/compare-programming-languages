package com.pipiobjo.domain.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchParams {
    public final static String URL_SEARCH_PARAM_NAME_LOGIN="login";
    public final static String URL_SEARCH_PARAM_NAME_FIRSTNAME="firstname";
    public final static String URL_SEARCH_PARAM_NAME_LASTNAME="lastname";
    private String login;
    private String firstName;
    private String lastName;
}
