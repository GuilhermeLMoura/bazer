package bazer.domain.address.dto;

public record AddressReadDto(
        Long id,
        String postalCode,
        String addressNumber,
        String state,
        String neighborhood,
        String city
) {
}
