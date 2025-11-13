package org.skypro.socksStock.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Интеграционные тесты контроллера управления пользователями для администратора")
public class UserAdminControllerIntegrationTest extends BaseApiTest {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_REGISTER_PATH = AUTH_PATH + "/register";
    private static final String AUTH_REGISTER_ADMIN_PATH = AUTH_PATH + "/register-admin";
    private static final String ADMIN_USERS_PATH = "/admin/users";
    private static final String ADMIN_USERS_ID_PATH = ADMIN_USERS_PATH + "/{id}";
    private static final String ADMIN_USERS_USERNAME_PATH = ADMIN_USERS_PATH + "/username/{username}";

    @Test
    @DisplayName("Получение всех пользователей при их наличии должно возвращать список пользователей")
    void getAllUsers_WhenUsersExist_ShouldReturnsUsers() {
        String username = "User";
        String password = "Password";

        String usernameAdmin = "Admin";
        String passwordAdmin = "PasswordAdmin";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecParam())
                .param("username", usernameAdmin)
                .param("password", passwordAdmin)
                .when()
                .post(AUTH_REGISTER_ADMIN_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecJson())
                .get(ADMIN_USERS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("username", hasItems("admin", username, usernameAdmin))
                .body("role", hasItems("ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    @DisplayName("Получение всех пользователей администратором должно возвращать список пользователей")
    void getAllUsers_WhenCalledByAdmin_ShouldReturnUsersList() {
        given()
                .spec(getSpecJson())
                .get(ADMIN_USERS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("username", hasItem("admin"))
                .body("findAll { it.username == 'admin' }.role", hasItem("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Получение всех пользователей без аутентификации должно возвращать статус FORBIDDEN")
    void getAllUsers_WhenNotAuthenticated_ShouldReturnForbidden() {
        given()
                .spec(specJsonBuilder().build())
                .get(ADMIN_USERS_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Получение всех пользователей обычным пользователем должно возвращать статус FORBIDDEN")
    void getAllUsers_WhenRegularUser_ShouldReturnForbidden() {
        String userToken = given()
                .spec(getSpecParam())
                .param("username", "regularUser")
                .param("password", "password")
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");

        given()
                .spec(specJsonBuilder()
                        .addHeader("Authorization", "Bearer " + userToken)
                        .build())
                .get(ADMIN_USERS_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Получение пользователя по ID при наличии разных пользователей должно возвращать корректных пользователей")
    void getUserById_WhenDifferentUsersExist_ShouldReturnCorrectUsers() {
        String regularUsername = "RegularUser";
        String regularPassword = "Password123";

        given()
                .spec(getSpecParam())
                .param("username", regularUsername)
                .param("password", regularPassword)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        int regularUserId = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == '" + regularUsername + "' }.id");

        int adminUserId = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == 'admin' }.id");

        given()
                .spec(getSpecJson())
                .pathParam("id", regularUserId)
                .when()
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(regularUserId))
                .body("username", equalTo(regularUsername))
                .body("role", equalTo("ROLE_USER"));

        given()
                .spec(getSpecJson())
                .pathParam("id", adminUserId)
                .when()
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(adminUserId))
                .body("username", equalTo("admin"))
                .body("role", equalTo("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Получение пользователя по несуществующему ID должно возвращать статус NOT_FOUND")
    void getUserById_WhenNotUser_ShouldNotFound() {
        given()
                .spec(getSpecJson())
                .pathParam("id", 99999L)
                .when()
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Получение пользователя по ID обычным пользователем должно возвращать статус FORBIDDEN")
    void getUserById_WhenRegularUser_ShouldReturnForbidden() {
        String userToken = given()
                .spec(getSpecParam())
                .param("username", "regularUser")
                .param("password", "password")
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");

        given()
                .spec(specJsonBuilder()
                        .addHeader("Authorization", "Bearer " + userToken)
                        .build())
                .pathParam("id", 1L)
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Получение пользователя по имени пользователя при его наличии должно возвращать пользователя")
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        String username = "RegularUser";
        String regularPassword = "Password123";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", regularPassword)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        int userId = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == '" + username + "' }.id");

        int adminId = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == 'admin' }.id");

        given()
                .spec(getSpecJson())
                .pathParam("username", "admin")
                .when()
                .get(ADMIN_USERS_USERNAME_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(adminId))
                .body("username", equalTo("admin"))
                .body("role", equalTo("ROLE_ADMIN"));

        given()
                .spec(getSpecJson())
                .pathParam("username", username)
                .when()
                .get(ADMIN_USERS_USERNAME_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(userId))
                .body("username", equalTo(username))
                .body("role", equalTo("ROLE_USER"));
    }

    @Test
    @DisplayName("Получение пользователя по несуществующему имени пользователя должно возвращать статус NOT_FOUND")
    void getUserByUsername_WhenNotUser_ShouldTReturnNotFound() {
        given()
                .spec(getSpecJson())
                .pathParam("username", "AAA")
                .when()
                .get(ADMIN_USERS_USERNAME_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Получение пользователя по имени пользователя обычным пользователем должно возвращать статус FORBIDDEN")
    void getUserByUsername_WhenRegularUser_ShouldReturnForbidden() {
        String userToken = given()
                .spec(getSpecParam())
                .param("username", "regularUser")
                .param("password", "password")
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");

        given()
                .spec(specJsonBuilder()
                        .addHeader("Authorization", "Bearer " + userToken)
                        .build())
                .when()
                .pathParam("username", "regularUser")
                .get(ADMIN_USERS_USERNAME_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Удаление пользователя при его наличии должно возвращать статус OK")
    void deleteUser_WhenUserExists_ShouldReturnOk() {
        String username = "User";
        String password = "Password123";

        given()
                .spec(getSpecParam())
                .param("username", username)
                .param("password", password)
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        int userId = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == '" + username + "' }.id");

        given()
                .spec(getSpecJson())
                .pathParam("id", userId)
                .when()
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecJson())
                .pathParam("id", userId)
                .when()
                .delete(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecJson())
                .pathParam("id", userId)
                .when()
                .get(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя должно возвращать статус NOT_FOUND")
    void deleteUser_WhenNotUser_ShouldReturnOk() {
        given()
                .spec(getSpecJson())
                .pathParam("id", 999999L)
                .when()
                .delete(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Удаление пользователя обычным пользователем должно возвращать статус FORBIDDEN")
    void deleteUser_WhenRegularUser_ShouldReturnForbidden() {
        String regularUserToken = given()
                .spec(getSpecParam())
                .param("username", "regularUserForDelete")
                .param("password", "password")
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");

        String userToDelete = "userToDelete";
        given()
                .spec(getSpecParam())
                .param("username", userToDelete)
                .param("password", "password")
                .when()
                .post(AUTH_REGISTER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        int userIdToDelete = given()
                .spec(getSpecJson())
                .when()
                .get(ADMIN_USERS_PATH)
                .then()
                .extract()
                .jsonPath()
                .getInt("find { it.username == '" + userToDelete + "' }.id");

        given()
                .spec(specJsonBuilder()
                        .addHeader("Authorization", "Bearer " + regularUserToken)
                        .build())
                .pathParam("id", userIdToDelete)
                .when()
                .delete(ADMIN_USERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
