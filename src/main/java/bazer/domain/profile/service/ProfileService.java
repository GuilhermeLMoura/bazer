package bazer.domain.profile.service;

import bazer.domain.address.entity.Address;
import bazer.domain.address.repository.AddressRepository;
import bazer.domain.profile.dto.ProfileRegisterDto;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.user.dto.UserCreateDto;
import bazer.domain.user.entity.EnumRole;
import bazer.domain.user.entity.User;
import bazer.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Transactional
    public Profile create(ProfileRegisterDto dto, MultipartFile photo) {
        if (dto.getRole() == EnumRole.ADMIN) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                throw new AccessDeniedException("Apenas administradores podem criar usuários administradores.");
            }
        }

        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(dto.getUsername());
        userCreateDto.setPassword(dto.getPassword());
        userCreateDto.setRole(dto.getRole());
        User user = userService.create(userCreateDto);

        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            photoUrl = fileStorageService.store(photo);
        }

        Profile profile = new Profile();
        profile.setName(dto.getName());
        profile.setDocument(dto.getDocument());
        profile.setPhoto(photoUrl);
        profile.setPhone(dto.getPhone());
        profile.setUser(user);
        profile = profileRepository.save(profile);

        Address address = new Address();
        address.setPostalCode(dto.getPostalCode());
        address.setAddressNumber(dto.getAddressNumber());
        address.setState(dto.getState());
        address.setNeighborhood(dto.getNeighborhood());
        address.setCity(dto.getCity());
        address.setProfile(profile);
        addressRepository.save(address);

        return profile;
    }

    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
    }

    public Profile findByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado para o usuário informado"));
    }

    public List<Profile> searchStores(String name) {
        return profileRepository.searchByRole(EnumRole.VENDEDOR, name);
    }
}
