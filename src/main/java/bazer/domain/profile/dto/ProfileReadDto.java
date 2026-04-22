package bazer.domain.profile.dto;

public record ProfileReadDto(
        Long id,
        String name,
        String document,
        String photo,
        String phone
) {
}
