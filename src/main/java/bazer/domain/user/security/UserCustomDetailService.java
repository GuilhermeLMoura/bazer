package bazer.domain.user.security;

import bazer.domain.user.entity.User;
import bazer.domain.user.repository.UserRepository;
import bazer.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserCustomDetailService implements UserDetailsService {
    private final UserRepository repository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Override
    public UserCustomDetail loadUserByUsername(String username) {

        Optional<User> user = repository.findByUsername(username);

        if(user.isEmpty()){
            LOGGER.error("Usuário não encontrado no banco: {}", username);
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return new UserCustomDetail(user.get());
    }
}
