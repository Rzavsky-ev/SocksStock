package org.skypro.socksStock.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.security.CustomUserDetailsService;
import org.skypro.socksStock.security.JwtTokenProvider;
import org.skypro.socksStock.service.AuthService;
import org.skypro.socksStock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private AuthService authServiceMock;

    @MockitoBean
    private JwtTokenProvider jwtTokenProviderMock;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsServiceMock;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPass";
    private static final Role TEST_USER_ROLE = Role.ROLE_USER;
    private static final Role TEST_ADMIN_ROLE = Role.ROLE_ADMIN;

    @DisplayName("Получение всех пользователей - должен вернуть список пользователей")
    @Test
    void getAllUsersWhenUsersExistReturnUserList() throws Exception {

        AppUser user1 = createTestUser(1L, "user1", Role.ROLE_USER);
        AppUser user2 = createTestUser(2L, "user2", Role.ROLE_ADMIN);
        List<AppUser> users = Arrays.asList(user1, user2);

        given(userServiceMock.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].role").value("ROLE_ADMIN"));

        then(userServiceMock).should().getAllUsers();
    }

    @DisplayName("Получение всех пользователей, когда пользователей нет - должен вернуть пустой список")
    @Test
    void getAllUsersWhenNoUsersReturnEmptyList() throws Exception {
        given(userServiceMock.getAllUsers()).willReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        then(userServiceMock).should().getAllUsers();
    }

    @DisplayName("Получение пользователя по ID - должен вернуть пользователя")
    @Test
    void getUserByIdWhenUserExistsReturnUser() throws Exception {
        AppUser user = createTestUser(TEST_USER_ID, TEST_USERNAME, TEST_USER_ROLE);
        given(userServiceMock.getUserById(TEST_USER_ID)).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/admin/users/{id}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.role").value(TEST_USER_ROLE.toString()));

        then(userServiceMock).should().getUserById(TEST_USER_ID);
    }

    @DisplayName("Получение пользователя по несуществующему ID - должен вернуть 404")
    @Test
    void getUserByIdWhenUserNotExistsReturnNotFound() throws Exception {
        given(userServiceMock.getUserById(TEST_USER_ID)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/{id}", TEST_USER_ID))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().getUserById(TEST_USER_ID);
    }

    @DisplayName("Получение пользователя по имени - должен вернуть пользователя")
    @Test
    void getUserByUsernameWhenUserExistsReturnUser() throws Exception {
        AppUser user = createTestUser(TEST_USER_ID, TEST_USERNAME, TEST_USER_ROLE);
        given(userServiceMock.getUserByUsername(TEST_USERNAME)).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/admin/users/username/{username}", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.role").value(TEST_USER_ROLE.toString()));

        then(userServiceMock).should().getUserByUsername(TEST_USERNAME);
    }

    @DisplayName("Получение пользователя по несуществующему имени - должен вернуть 404")
    @Test
    void getUserByUsernameWhenUserNotExistsReturnNotFound() throws Exception {
        given(userServiceMock.getUserByUsername(TEST_USERNAME)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/username/{username}", TEST_USERNAME))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().getUserByUsername(TEST_USERNAME);
    }

    @DisplayName("Обновление роли пользователя - должен вернуть обновленного пользователя")
    @Test
    void updateUserRoleWhenUserExistsReturnUpdatedUser() throws Exception {
        AppUser updatedUser = createTestUser(TEST_USER_ID, TEST_USERNAME, TEST_ADMIN_ROLE);
        given(userServiceMock.updateUserRole(eq(TEST_USER_ID), eq(TEST_ADMIN_ROLE)))
                .willReturn(updatedUser);

        mockMvc.perform(put("/api/admin/users/{id}/role", TEST_USER_ID)
                        .with(csrf())
                        .param("role", TEST_ADMIN_ROLE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.role").value(TEST_ADMIN_ROLE.toString()));

        then(userServiceMock).should().updateUserRole(TEST_USER_ID, TEST_ADMIN_ROLE);
    }

    @DisplayName("Обновление роли несуществующего пользователя - должен вернуть 404")
    @Test
    void updateUserRoleWhenUserNotExistsReturnNotFound() throws Exception {
        given(userServiceMock.updateUserRole(eq(TEST_USER_ID), any(Role.class)))
                .willThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/admin/users/{id}/role", TEST_USER_ID)
                        .with(csrf())
                        .param("role", TEST_ADMIN_ROLE.toString()))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().updateUserRole(TEST_USER_ID, TEST_ADMIN_ROLE);
    }

    @DisplayName("Обновление роли с неверным значением роли - должен вернуть ошибку")
    @Test
    void updateUserRoleWhenInvalidRoleReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/{id}/role", TEST_USER_ID)
                        .with(csrf())
                        .param("role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest());

        then(userServiceMock).should(never()).updateUserRole(anyLong(), any(Role.class));
    }

    @DisplayName("Удаление пользователя - должен вернуть 200 OK")
    @Test
    void deleteUserWhenUserExistsReturnOk() throws Exception {

        mockMvc.perform(delete("/api/admin/users/{id}", TEST_USER_ID)
                        .with(csrf()))
                .andExpect(status().isOk());

        then(userServiceMock).should().deleteUser(TEST_USER_ID);
    }

    @DisplayName("Удаление несуществующего пользователя - должен вернуть 404")
    @Test
    void deleteUser_WhenUserNotExists_ShouldReturnNotFound() throws Exception {

        willThrow(new RuntimeException("User not found"))
                .given(userServiceMock).deleteUser(TEST_USER_ID);

        mockMvc.perform(delete("/api/admin/users/{id}", TEST_USER_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().deleteUser(TEST_USER_ID);
    }

    @DisplayName("Удаление пользователя с нулевым ID - должен вернуть 404")
    @Test
    void deleteUserWhenZeroIdReturnNotFound() throws Exception {
        willThrow(new RuntimeException("User not found"))
                .given(userServiceMock).deleteUser(0L);

        mockMvc.perform(delete("/api/admin/users/{id}", 0)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().deleteUser(0L);
    }

    @DisplayName("Проверка существования пользователя - пользователь существует, должен вернуть true")
    @Test
    void checkUserExistsWhenUserExistsReturnTrue() throws Exception {
        given(userServiceMock.userExists(TEST_USERNAME)).willReturn(true);

        mockMvc.perform(get("/api/admin/users/check/{username}", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        then(userServiceMock).should().userExists(TEST_USERNAME);
    }

    @DisplayName("Проверка существования пользователя - пользователь не существует, должен вернуть false")
    @Test
    void checkUserExistsWhenUserNotExistsReturnFalse() throws Exception {
        given(userServiceMock.userExists(TEST_USERNAME)).willReturn(false);

        mockMvc.perform(get("/api/admin/users/check/{username}", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        then(userServiceMock).should().userExists(TEST_USERNAME);
    }

    @DisplayName("Получение пользователей по роли - должен вернуть список пользователей")
    @Test
    void getUsersByRoleWhenUsersWithRoleExistReturnUserList() throws Exception {
        AppUser user1 = createTestUser(1L, "user1", TEST_USER_ROLE);
        AppUser user2 = createTestUser(2L, "user2", TEST_USER_ROLE);
        List<AppUser> users = Arrays.asList(user1, user2);

        given(userServiceMock.getUsersByRole(TEST_USER_ROLE)).willReturn(users);

        mockMvc.perform(get("/api/admin/users/role/{role}", TEST_USER_ROLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].role").value(TEST_USER_ROLE.toString()))
                .andExpect(jsonPath("$[1].role").value(TEST_USER_ROLE.toString()));

        then(userServiceMock).should().getUsersByRole(TEST_USER_ROLE);
    }

    @DisplayName("Получение пользователей по роли - нет пользователей с такой ролью, должен вернуть пустой список")
    @Test
    void getUsersByRoleWhenNoUsersWithRoleReturnEmptyList() throws Exception {
        given(userServiceMock.getUsersByRole(TEST_ADMIN_ROLE)).willReturn(List.of());

        mockMvc.perform(get("/api/admin/users/role/{role}", TEST_ADMIN_ROLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        then(userServiceMock).should().getUsersByRole(TEST_ADMIN_ROLE);
    }

    @DisplayName("Получение пользователей по неверной роли - должен вернуть ошибку")
    @Test
    void getUsersByRoleWhenInvalidRoleReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/users/role/{role}", "INVALID_ROLE"))
                .andExpect(status().isBadRequest());

        then(userServiceMock).should(never()).getUsersByRole(any(Role.class));
    }

    @DisplayName("Получение пользователя по нулевому ID - должен вернуть ошибку")
    @Test
    void getUserByIdWhenZeroIdReturnNotFound() throws Exception {
        given(userServiceMock.getUserById(0L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/{id}", 0))
                .andExpect(status().isNotFound());

        then(userServiceMock).should().getUserById(0L);
    }

    private AppUser createTestUser(Long id, String username, Role role) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(TEST_PASSWORD);
        user.setRole(role);
        return user;
    }
}