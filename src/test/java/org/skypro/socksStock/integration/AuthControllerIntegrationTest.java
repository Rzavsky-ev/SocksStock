package org.skypro.socksStock.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Интеграционные тесты контроллера аутентификации")
public class AuthControllerIntegrationTest extends BaseApiTest {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_REGISTER_PATH = AUTH_PATH + "/register";
    private static final String AUTH_REGISTER_ADMIN_PATH = AUTH_PATH + "/register-admin";
    private static final String AUTH_LOGIN_PATH = AUTH_PATH + "/login";

    @Test
    @DisplayName("Аутентификация пользователя с валидными данными должна возвращать JWT токен")
    void authenticateUser_WhenValidCredentials_ShouldReturnJwtToken() {
        String username = "UserTest";
        String password = "PasswordTest";

        String loginRequestBody = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(username, password);

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecJson())
                .body(loginRequestBody)
                .when()
                .post(AUTH_LOGIN_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"));
    }

    @Test
    @DisplayName("Аутентификация пользователя с невалидными данными должна возвращать статус UNAUTHORIZED")
    void authenticateUser_WhenInvalidCredentials_ShouldReturnUnauthorized() {
        String loginRequestBody = """
                {
                    "username": "invalidUsername",
                    "password": "invalidPassword"
                }
                """;

        given()
                .spec(getSpecJson())
                .body(loginRequestBody)
                .when()
                .post(AUTH_LOGIN_PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Регистрация пользователя с валидными данными должна возвращать JWT токен")
    void registerUser_WhenValidData_ShouldReturnJwtToken() {
        String username = "UserTest";
        String password = "PasswordTest";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"));
    }

    @Test
    @DisplayName("Регистрация пользователя с дублирующимся именем должна возвращать статус CONFLICT")
    void registerUser_WithDuplicateUsername_ShouldReturnConflict() {
        String username = "UserTest";
        String password = "PasswordTest";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("token", not(emptyString()));

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Регистрация администратора с валидными данными должна возвращать JWT токен")
    void registerAdmin_WhenValidData_ShouldReturnJwtToken() {
        String username = "AdminTest";
        String password = "PasswordTest";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_ADMIN_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"));
    }

    @Test
    @DisplayName("Регистрация администратора с дублирующимся именем должна возвращать статус CONFLICT")
    void registerAdmin_WithDuplicateUsername_ShouldReturnConflict() {
        String username = "AdminTest";
        String password = "PasswordTest";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_ADMIN_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"));

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_ADMIN_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}