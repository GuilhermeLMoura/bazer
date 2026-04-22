package bazer.domain.profile.controller;

import bazer.domain.profile.dto.ProfileReadDto;
import bazer.domain.profile.dto.ProfileRegisterDto;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Cadastro de novo usuário/perfil no sistema (comprador, vendedor ou admin).
     * Aceita foto de perfil como arquivo (multipart/form-data).
     * Endpoint público — não requer autenticação.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileReadDto> create(
            @Valid @ModelAttribute ProfileRegisterDto dto,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) {
        Profile profile = profileService.create(dto, photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(profile));
    }

    /**
     * Busca o perfil pelo ID do perfil.
     * Usado para exibir página de detalhes de comprador ou vendedor.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(profileService.findById(id)));
    }

    /**
     * Busca o perfil vinculado a um usuário específico pelo ID do usuário.
     * Útil para redirecionar ao perfil logo após o login.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileReadDto> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(toDto(profileService.findByUserId(userId)));
    }

    /**
     * Busca lojas (perfis com role VENDEDOR) por nome.
     * Usado na busca da vitrine do e-commerce.
     * Endpoint público — não requer autenticação.
     * Parâmetro opcional: ?name=texto
     */
    @GetMapping("/searchStore")
    public ResponseEntity<List<ProfileReadDto>> searchStore(
            @RequestParam(required = false) String name
    ) {
        List<ProfileReadDto> stores = profileService.searchStores(name)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(stores);
    }

    private ProfileReadDto toDto(Profile profile) {
        return new ProfileReadDto(
                profile.getId(),
                profile.getName(),
                profile.getDocument(),
                profile.getPhoto(),
                profile.getPhone()
        );
    }
}
