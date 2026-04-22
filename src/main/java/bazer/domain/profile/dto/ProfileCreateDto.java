package bazer.domain.profile.dto;

public record ProfileCreateDto(
        String name,
        String document,
        String photo,
        String phone
) {
}
