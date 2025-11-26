package com.example.minieticaret.testsupport;

import com.example.minieticaret.auth.domain.Role;
import com.example.minieticaret.auth.domain.RoleName;
import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.auth.domain.UserStatus;
import com.example.minieticaret.auth.repository.RoleRepository;
import com.example.minieticaret.auth.repository.UserRepository;
import com.example.minieticaret.catalog.domain.Category;
import com.example.minieticaret.catalog.domain.Product;
import com.example.minieticaret.catalog.domain.ProductStatus;
import com.example.minieticaret.catalog.repository.CategoryRepository;
import com.example.minieticaret.catalog.repository.ProductRepository;
import com.example.minieticaret.customer.domain.Address;
import com.example.minieticaret.customer.repository.AddressRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Component
public class TestDataFactory {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public TestDataFactory(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           AddressRepository addressRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    public Role ensureRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    public User ensureUser(String email, String firstName, String lastName, String passwordHash, Set<RoleName> roles) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            Set<Role> roleEntities = roles.stream()
                    .map(this::ensureRole)
                    .collect(java.util.stream.Collectors.toSet());
            User user = User.builder()
                    .email(email)
                    .passwordHash(passwordHash)
                    .firstName(firstName)
                    .lastName(lastName)
                    .status(UserStatus.ACTIVE)
                    .roles(roleEntities)
                    .build();
            return userRepository.save(user);
        });
    }

    public Category ensureCategory(String name, String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .slug(slug)
                        .build()));
    }

    public Product ensureProduct(String sku, Category category, BigDecimal price, String currency, int stock) {
        return productRepository.findBySku(sku)
                .orElseGet(() -> productRepository.save(Product.builder()
                        .name("Product-" + sku)
                        .description("Test product")
                        .price(price)
                        .currency(currency)
                        .sku(sku)
                        .category(category)
                        .stockQuantity(stock)
                        .status(ProductStatus.ACTIVE)
                        .images(new java.util.ArrayList<>(java.util.List.of("http://img")))
                        .build()));
    }

    public Address ensureAddress(UUID userId, String line1, String city, String country, String zip, String phone) {
        return addressRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getId().equals(userId))
                .findFirst()
                .orElseGet(() -> addressRepository.save(Address.builder()
                        .user(userRepository.findById(userId).orElseThrow())
                        .line1(line1)
                        .line2(null)
                        .city(city)
                        .country(country)
                        .zip(zip)
                        .phone(phone)
                        .build()));
    }
}
