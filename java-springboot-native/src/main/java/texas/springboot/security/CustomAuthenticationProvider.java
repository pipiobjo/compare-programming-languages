package texas.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import texas.springboot.user.UserService;
import texas.springboot.user.model.UserDTO;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    @Autowired
    public CustomAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDTO userByLogin = userService.getUserByLogin(name);
        if (userByLogin == null) {
            return null;
        }
        if (!userByLogin.getPassword().equals(password)) {
            return null;
        }else{
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());

        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
