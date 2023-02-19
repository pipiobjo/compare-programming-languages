package texas.springboot.greeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import texas.springboot.greeting.model.GreetingDTO;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private final GreetingService greetingService;
    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }


    @GetMapping
    public ResponseEntity greeting(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if(principal != null && principal instanceof String){
            String username = (String) principal;
            GreetingDTO greet = greetingService.greet(username);
            if(greet != null){
                return ResponseEntity.ok(greet);
            }
            return ResponseEntity.ok("Hello World");
        }
            return ResponseEntity.badRequest().build();
    }
}
