package com.example.minieticaret.customer.repository;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.customer.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
}
