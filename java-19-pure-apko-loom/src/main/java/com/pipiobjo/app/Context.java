package com.pipiobjo.app;

import com.pipiobjo.webserver.errors.GlobalExceptionHandler;
import com.pipiobjo.data.user.InMemoryUserRepository;
import com.pipiobjo.domain.user.UserRepository;
import com.pipiobjo.domain.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Context {
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);
    private Configuration config;
    private final UserRepository USER_REPOSITORY;
    private final UserService USER_SERVICE;

    // override builder build method
    public static ContextBuilder builder(){
        return new CustomContextBuilder();
    }

    private static class CustomContextBuilder extends ContextBuilder{
        public Context build(){
            if(super.config.isUseInMemoryUserDB() == true){
                super.USER_REPOSITORY = new InMemoryUserRepository();
            }else{
                throw new RuntimeException("invalid configuration");
            }
            super.USER_SERVICE(new UserService(super.USER_REPOSITORY));

            return super.build();
        }
    }

}
