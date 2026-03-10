package bazer.domain.user.controller;

import bazer.configuration.Security.AuthService;
import bazer.domain.user.dto.UserCreateDto;
import bazer.domain.user.dto.UserLogin;
import bazer.domain.user.dto.UserToken;
import bazer.domain.user.model.User;
import bazer.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/criar")
    public ResponseEntity<Void> create(@RequestBody UserCreateDto userDto){
        User user = userService.create(userDto);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserToken> login(@RequestBody UserLogin userLogin){

        UserToken userToken = authService.login(userLogin);
        return ResponseEntity.ok(userToken);
    }
}