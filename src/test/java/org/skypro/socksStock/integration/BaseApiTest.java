package org.skypro.socksStock.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * Базовый класс для API тестов
 * Предоставляет общую конфигурацию и утилитные методы для тестирования REST API
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseApiTest {


    @LocalServerPort
    private int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SocksRepository socksRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String jwtToken;

    private static final String BASE_PATH = "/api";
    private static final String BASE_URI = "http://localhost";
    private static final String AUTH_LOGIN_PATH = "/auth/login";

    /**
     * Метод настройки, выполняемый перед каждым тестом
     * Выполняет последовательную инициализацию тестового окружения:
     * 1. Настройка RestAssured
     * 2. Очистка базы данных
     * 3. Создание тестового администратора
     * 4. Получение JWT токена для аутентификации
     */
    @BeforeEach
    void setUp() {
        // Настройка базовых параметров RestAssured
        RestAssured.port = port;
        RestAssured.baseURI = BASE_URI;

        // Очистка базы данных от предыдущих тестовых данных
        clearDatabase();

        // Создание тестового пользователя с правами администратора
        createTestAdmin();

        // Аутентификация и получение JWT токена для последующих запросов
        authenticateAndGetToken();
    }

    /**
     * Создает тестового пользователя с ролью администратора
     * Если пользователь уже существует, метод пропускает создание
     */
    protected void createTestAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
        }
    }

    /**
     * Выполняет аутентификацию и получает JWT токен для тестового администратора
     * Токен сохраняется в поле jwtToken для использования в защищенных запросах
     */
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
        } catch (Exception e) {
            jwtToken = null;
        }
    }

    /**
     * Создает билдер спецификации для JSON запросов
     *
     * @return RequestSpecBuilder с настройками для JSON контента
     */
    protected RequestSpecBuilder specJsonBuilder() {
        return new RequestSpecBuilder()
                .setBasePath(BASE_PATH)
                .setPort(port)
                .setContentType(ContentType.JSON);
    }

    /**
     * Создает билдер спецификации для параметризованных запросов (URL encoded)
     *
     * @return RequestSpecBuilder с настройками для URL encoded контента
     */
    protected RequestSpecBuilder specParamBuilder() {
        return new RequestSpecBuilder()
                .setBasePath(BASE_PATH)
                .setPort(port)
                .setContentType(ContentType.URLENC);
    }

    /**
     * Возвращает спецификацию для JSON запросов с JWT аутентификацией
     * Если токен доступен, добавляет заголовок Authorization
     *
     * @return RequestSpecification для JSON запросов
     */
    protected RequestSpecification getSpecJson() {
        RequestSpecBuilder builder = specJsonBuilder();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            return builder
                    .addHeader("Authorization", "Bearer " + jwtToken)  // Добавление JWT токена
                    .build();
        }
        return builder.build();
    }

    /**
     * Возвращает спецификацию для параметризованных запросов с JWT аутентификацией
     * Если токен доступен, добавляет заголовок Authorization
     *
     * @return RequestSpecification для параметризованных запросов
     */
    protected RequestSpecification getSpecParam() {
        RequestSpecBuilder builder = specParamBuilder();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            return builder
                    .addHeader("Authorization", "Bearer " + jwtToken)  // Добавление JWT токена
                    .build();
        }
        return builder.build();
    }

    /**
     * Очищает базу данных от всех тестовых данных
     * Удаляет все записи о носках и пользователях
     * Гарантирует чистоту тестового окружения
     */
    private void clearDatabase() {
        socksRepository.deleteAll();
        userRepository.deleteAll();
    }
}