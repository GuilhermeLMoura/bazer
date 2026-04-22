package bazer.configuration.Security;

import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.user.dto.UserLogin;
import bazer.domain.user.dto.UserToken;
import bazer.domain.user.security.UserCustomDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    public UserToken login(UserLogin userLogin){

        try {

            UsernamePasswordAuthenticationToken userAuth =
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getUsername(),
                            userLogin.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(userAuth);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserCustomDetail userDetail = (UserCustomDetail) authentication.getPrincipal();
            String token = jwtUtil.GenerateToken(authentication);

            String profileName = profileRepository.findByUserUsername(userDetail.getUsername())
                    .map(p -> p.getName())
                    .orElse(null);

            return new UserToken(
                    userDetail.getId(),
                    userDetail.getUsername(),
                    profileName,
                    userDetail.getRole().name(),
                    token
            );

        } catch (AuthenticationException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário ou senha inválidos"
            );
        }
    }
}