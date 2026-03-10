package bazer.domain.user.service;

import bazer.domain.user.dto.UserCreateDto;
import bazer.domain.user.model.EnumStatus;
import bazer.domain.user.model.User;
import bazer.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
// -> Class responsible for transforming database users into UserDetails.
public class UserService {
    private final UserRepository repository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public User create(UserCreateDto userCreateDto){
        User user;
        user = modelMapper.map(userCreateDto, User.class);
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setStatus(EnumStatus.ATIVO);
        user.setRole(userCreateDto.getRole());
        return repository.save(user);
    }
}
