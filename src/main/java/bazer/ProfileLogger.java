package bazer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfileLogger {
    @Autowired
    private Environment env;

    @PostConstruct
    public void showProfile(){
        System.out.println("Customer ACTIVE: " + Arrays.toString(env.getActiveProfiles()));
    }
}
