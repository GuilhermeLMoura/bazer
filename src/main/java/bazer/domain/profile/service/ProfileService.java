package bazer.domain.profile.service;

import bazer.domain.address.entity.Address;
import bazer.domain.address.repository.AddressRepository;
import bazer.domain.commission.entity.Commission;
import bazer.domain.commission.repository.CommissionRepository;
import bazer.domain.profile.dto.ProfileRegisterDto;
import bazer.domain.profile.dto.ProfileUpdateDto;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.user.dto.UserCreateDto;
import bazer.domain.user.entity.EnumRole;
import bazer.domain.user.entity.User;
import bazer.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final CommissionRepository commissionRepository;
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

        if (dto.getCommissionId() != null) {
            Commission commission = commissionRepository.findById(dto.getCommissionId())
                    .orElseThrow(() -> new EntityNotFoundException("Comissão não encontrada: " + dto.getCommissionId()));
            profile.setCommission(commission);
        }

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

    @Transactional
    public Profile update(ProfileUpdateDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (dto.getName() != null) profile.setName(dto.getName());
        if (dto.getDocument() != null) profile.setDocument(dto.getDocument());
        if (dto.getPhone() != null) profile.setPhone(dto.getPhone());

        return profileRepository.save(profile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Profile assignCommission(Long profileId, Long commissionId) {
        Profile profile = findById(profileId);
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new EntityNotFoundException("Comissão não encontrada: " + commissionId));
        profile.setCommission(commission);
        return profileRepository.save(profile);
    }
}
