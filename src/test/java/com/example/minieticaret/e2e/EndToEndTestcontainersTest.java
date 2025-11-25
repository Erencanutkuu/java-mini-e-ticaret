package com.example.minieticaret.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testcontainers")
@ContextConfiguration(initializers = EndToEndTestcontainersTest.Initializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EndToEndTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("e2e_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String adminToken;
    private UUID categoryId;
    private UUID productId;
    private UUID addressId;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.flyway.enabled=true",
                    "spring.jpa.hibernate.ddl-auto=validate",
                    "spring.jpa.open-in-view=false"
            ).applyTo(context.getEnvironment());
        }
    }

    @BeforeAll
    void setupUsers() throws Exception {
        registerUser("user@example.com", "password", "User", "Test");
        accessToken = loginAndGetToken("user@example.com", "password");
    }

    @Test
    void endToEnd_checkout_and_refund_flow() throws Exception {
        registerUser("admin@example.com", "password", "Admin", "Test");
        promoteToAdmin("admin@example.com");
        adminToken = loginAndGetToken("admin@example.com", "password");

        String categoryResponse = mockMvc.perform(post("/api/catalog/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "E2E",
                                "slug", "e2e"
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        categoryId = UUID.fromString(objectMapper.readTree(categoryResponse).get("id").asText());

        String productResponse = mockMvc.perform(post("/api/catalog/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "E2E Product",
                                "description", "",
                                "price", "50.00",
                                "currency", "TRY",
                                "sku", "E2E-1",
                                "categoryId", categoryId.toString(),
                                "stockQuantity", 5,
                                "status", "ACTIVE",
                                "images", java.util.List.of("http://img")
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        productId = UUID.fromString(objectMapper.readTree(productResponse).get("id").asText());

        String addressResponse = mockMvc.perform(post("/api/customer/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "line1", "Line1",
                                "line2", "",
                                "city", "Istanbul",
                                "country", "TR",
                                "zip", "34000",
                                "phone", "555"
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        addressId = UUID.fromString(objectMapper.readTree(addressResponse).get("id").asText());

        mockMvc.perform(post("/api/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "productId", productId.toString(),
                                "quantity", 2
                        ))))
                .andExpect(status().isCreated());

        String orderResponse = mockMvc.perform(post("/api/orders/checkout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("addressId", addressId.toString()))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode orderNode = objectMapper.readTree(orderResponse);
        UUID orderId = UUID.fromString(orderNode.get("id").asText());
        assertThat(orderNode.get("status").asText()).isEqualTo("PENDING");

        String captured = mockMvc.perform(post("/api/payments/mock/" + orderId + "/capture")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(captured).get("status").asText()).isEqualTo("PAID");

        String refunded = mockMvc.perform(post("/api/payments/mock/" + orderId + "/refund")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(refunded).get("status").asText()).isEqualTo("REFUNDED");
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private void registerUser(String email, String password, String first, String last) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password,
                                "firstName", first,
                                "lastName", last
                        ))))
                .andExpect(status().isCreated());
    }

    private void promoteToAdmin(String email) throws Exception {
        String sql = "insert into user_roles(user_id, role_id) " +
                "select u.id, r.id from users u, roles r " +
                "where u.email='" + email + "' and r.name='ADMIN' " +
                "on conflict do nothing";
        try (var conn = java.sql.DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
