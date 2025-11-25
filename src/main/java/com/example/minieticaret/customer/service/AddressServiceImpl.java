package com.example.minieticaret.customer.service;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.auth.repository.UserRepository;
import com.example.minieticaret.customer.domain.Address;
import com.example.minieticaret.customer.dto.AddressRequest;
import com.example.minieticaret.customer.dto.AddressResponse;
import com.example.minieticaret.customer.mapper.AddressMapper;
import com.example.minieticaret.customer.repository.AddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository,
                              AddressMapper addressMapper,
                              UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AddressResponse create(AddressRequest request, UUID userId) {
        User user = findUser(userId);
        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse update(UUID id, AddressRequest request, UUID userId) {
        Address address = findOwnedAddress(id, userId);
        addressMapper.update(request, address);
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID userId) {
        Address address = findOwnedAddress(id, userId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(UUID userId) {
        User user = findUser(userId);
        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    private Address findOwnedAddress(UUID id, UUID userId) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adres bulunamadi"));
        if (!address.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Adres size ait degil");
        }
        return address;
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanici bulunamadi"));
    }
}
