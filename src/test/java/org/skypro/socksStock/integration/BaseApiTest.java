package org.skypro.socksStock.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.SocksRepository;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Базовый класс для интеграционных тестов API управления складом носков")
public class BaseApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SocksRepository socksRepository;

    protected String jwtToken;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    private static final String BASE_PATH = "/api";
    private static final String BASE_URI = "http://localhost";
    private static final String AUTH_LOGIN_PATH = "/auth/login";

    @BeforeEach
    @DisplayName("Предварительная настройка: очистка БД, создание тестового администратора и получение JWT токена")
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = BASE_URI;

        clearDatabase();

        createTestAdmin();

        authenticateAndGetToken();
    }

    @DisplayName("Создание тестового пользователя с ролью администратора")
    protected void createTestAdmin() {
        try {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);
                System.out.println("Test admin created successfully");
            } else {
                System.out.println("Test admin already exists");
            }
        } catch (Exception e) {
            System.out.println("Failed to create test admin: " + e.getMessage());
        }
    }

    @DisplayName("Аутентификация администратора и получение JWT токена")
    protected void authenticateAndGetToken() {
        try {
            jwtToken = given()
                    .spec(specJsonBuilder().build())
                    .body("""
                            {
                                "username": "admin",
                                "password": "admin"
                            }
                            """)
                    .when()
                    .post(AUTH_LOGIN_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .path("token");
            System.out.println("JWT Token received: " + (jwtToken != null ? "YES" : "NO"));
        } catch (Exception e) {
            System.out.println("JWT auth failed: " + e.getMessage());
            jwtToken = null;
        }
    }

    @DisplayName("Создание спецификации запроса для JSON данных")
    protected RequestSpecBuilder specJsonBuilder() {
        return new RequestSpecBuilder()
                .setBasePath(BASE_PATH)
                .setPort(port)
                .setContentType(ContentType.JSON);
    }

    @DisplayName("Создание спецификации запроса для параметров URL")
    protected RequestSpecBuilder specParamBuilder() {
        return new RequestSpecBuilder()
                .setBasePath(BASE_PATH)
                .setPort(port)
                .setContentType(ContentType.URLENC);
    }

    @DisplayName("Получение спецификации JSON запроса с JWT авторизацией")
    protected RequestSpecification getSpecJson() {
        RequestSpecBuilder builder = specJsonBuilder();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            return builder
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();
        }
        return builder.build();
    }

    @DisplayName("Получение спецификации параметризованного запроса с JWT авторизацией")
    protected RequestSpecification getSpecParam() {
        RequestSpecBuilder builder = specParamBuilder();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            return builder
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();
        }
        return builder.build();
    }

    @DisplayName("Полная очистка тестовой базы данных")
    private void clearDatabase() {
        try {
            socksRepository.deleteAll();
            userRepository.deleteAll();
            System.out.println("Database cleared successfully");
        } catch (Exception e) {
            System.out.println("Database cleanup failed: " + e.getMessage());
        }
    }
}