package bazer.domain.address.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.address.dto.AddressCreateDto;
import bazer.domain.address.dto.AddressReadDto;
import bazer.domain.address.entity.Address;
import bazer.domain.address.repository.AddressRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public AddressReadDto create(AddressCreateDto dto) {
        Profile profile = getAuthenticatedProfile();
        Address address = new Address();
        address.setPostalCode(dto.postalCode());
        address.setAddressNumber(dto.addressNumber());
        address.setState(dto.state());
        address.setNeighborhood(dto.neighborhood());
        address.setCity(dto.city());
        address.setProfile(profile);
        return toDto(addressRepository.save(address));
    }

    public List<AddressReadDto> findAll() {
        Profile profile = getAuthenticatedProfile();
        return addressRepository.findByProfileId(profile.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AddressReadDto update(Long id, AddressCreateDto dto) {
        Address address = findOwnedAddress(id);
        address.setPostalCode(dto.postalCode());
        address.setAddressNumber(dto.addressNumber());
        address.setState(dto.state());
        address.setNeighborhood(dto.neighborhood());
        address.setCity(dto.city());
        return toDto(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id) {
        Address address = findOwnedAddress(id);
        addressRepository.delete(address);
    }

    private Address findOwnedAddress(Long id) {
        Profile profile = getAuthenticatedProfile();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado: " + id));
        if (!address.getProfile().getId().equals(profile.getId())) {
            throw new BusinessRuleException("Este endereço não pertence ao seu perfil.");
        }
        return address;
    }

    private Profile getAuthenticatedProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado para o usuário autenticado"));
    }

    public AddressReadDto toDto(Address address) {
        return new AddressReadDto(
                address.getId(),
                address.getPostalCode(),
                address.getAddressNumber(),
                address.getState(),
                address.getNeighborhood(),
                address.getCity()
        );
    }
}