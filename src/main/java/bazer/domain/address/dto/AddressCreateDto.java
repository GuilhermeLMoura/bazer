package bazer.domain.address.dto;

public record AddressCreateDto(
        String postalCode,
        String addressNumber,
        String state,
        String neighborhood,
        String city,
        Long profileId
) {
}
