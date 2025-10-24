package org.skypro.socksStock.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.exception.UserNotFoundException;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserService userServiceTest;

    private AppUser testUser;
    private AppUser testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRole(Role.ROLE_USER);

        testAdmin = new AppUser();
        testAdmin.setId(2L);
        testAdmin.setUsername("testAdmin");
        testAdmin.setRole(Role.ROLE_ADMIN);
    }

    @DisplayName("Должен вернуть всех пользователей при вызове getAllUsers")
    @Test
    void getAllUsersReturnAllUsers() {
        List<AppUser> expectedUsers = Arrays.asList(testUser, testAdmin);
        when(userRepositoryMock.findAll()).thenReturn(expectedUsers);

        List<AppUser> actualUsers = userServiceTest.getAllUsers();
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepositoryMock, times(1)).findAll();
    }

    @DisplayName("Должен вернуть пользователя при поиске по существующему ID")
    @Test
    void getUserByIdWithExistingIdReturnUser() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<AppUser> result = userServiceTest.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @DisplayName("Должен вернуть пустой Optional при поиске по несуществующему ID")
    @Test
    void getUserByIdWithNonExistingIdReturnEmpty() {
        when(userRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        Optional<AppUser> result = userServiceTest.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userRepositoryMock, times(1)).findById(999L);
    }

    @DisplayName("Должен вернуть пользователя при поиске по имени пользователя")
    @Test
    void getUserByUsernameWithExistingUsernameReturnUser() {
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        Optional<AppUser> result = userServiceTest.getUserByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepositoryMock, times(1)).findByUsername("testUser");
    }

    @DisplayName("Должен обновить роль пользователя при валидных данных")
    @Test
    void updateUserRoleWithValidDataUpdateRole() {
        Role newRole = Role.ROLE_ADMIN;
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser updatedUser = userServiceTest.updateUserRole(1L, newRole);

        assertNotNull(updatedUser);
        assertEquals(newRole, updatedUser.getRole());
        assertEquals(testUser.getId(), updatedUser.getId());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).save(testUser);
    }

    @DisplayName("Должен выбросить UserNotFoundException при обновлении роли несуществующего пользователя")
    @Test
    void updateUserRoleWithNonExistingUserThrowException() {
        Long nonExistingUserId = 999L;
        when(userRepositoryMock.findById(nonExistingUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userServiceTest.updateUserRole(nonExistingUserId, Role.ROLE_ADMIN));

        assertEquals("User not found with id: " + nonExistingUserId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepositoryMock, times(1)).findById(nonExistingUserId);
        verify(userRepositoryMock, never()).save(any());
    }

    @DisplayName("Должен удалить пользователя при существующем ID")
    @Test
    void deleteUserWithExistingIdDeleteUser() {
        when(userRepositoryMock.existsById(1L)).thenReturn(true);
        doNothing().when(userRepositoryMock).deleteById(1L);

        userServiceTest.deleteUser(1L);

        verify(userRepositoryMock, times(1)).existsById(1L);
        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @DisplayName("Должен выбросить UserNotFoundException при удалении несуществующего пользователя")
    @Test
    void deleteUserWithNonExistingIdThrowException() {
        Long nonExistingUserId = 999L;
        when(userRepositoryMock.existsById(nonExistingUserId)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userServiceTest.deleteUser(nonExistingUserId));

        assertEquals("User not found with id: " + nonExistingUserId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepositoryMock, times(1)).existsById(nonExistingUserId);
        verify(userRepositoryMock, never()).deleteById(nonExistingUserId);
    }

    @DisplayName("Должен вернуть true при проверке существующего пользователя")
    @Test
    void userExistsWithExistingUsernameReturnTrue() {
        when(userRepositoryMock.existsByUsername("testUser")).thenReturn(true);

        boolean exists = userServiceTest.userExists("testUser");

        assertTrue(exists);
        verify(userRepositoryMock, times(1)).existsByUsername("testUser");
    }

    @DisplayName("Должен вернуть false при проверке несуществующего пользователя")
    @Test
    void userExistsWithNonExistingUsernameReturnFalse() {
        when(userRepositoryMock.existsByUsername("nonexisting")).thenReturn(false);

        boolean exists = userServiceTest.userExists("nonexisting");

        assertFalse(exists);
        verify(userRepositoryMock, times(1)).existsByUsername("nonexisting");
    }

    @DisplayName("Должен вернуть пользователей с указанной ролью")
    @Test
    void getUsersByRoleReturnUsersWithSpecifiedRole() {
        List<AppUser> expectedUsers = Collections.singletonList(testAdmin);
        when(userRepositoryMock.findByRole(Role.ROLE_ADMIN)).thenReturn(expectedUsers);

        List<AppUser> actualUsers = userServiceTest.getUsersByRole(Role.ROLE_ADMIN);

        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
        assertEquals(Role.ROLE_ADMIN, actualUsers.get(0).getRole());
        verify(userRepositoryMock, times(1)).findByRole(Role.ROLE_ADMIN);
    }

    @DisplayName("Должен вернуть пустой список при отсутствии пользователей с указанной ролью")
    @Test
    void getUsersByRoleWithNoUsersReturnEmptyList() {
        when(userRepositoryMock.findByRole(Role.ROLE_USER)).thenReturn(List.of());

        List<AppUser> actualUsers = userServiceTest.getUsersByRole(Role.ROLE_USER);

        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userRepositoryMock, times(1)).findByRole(Role.ROLE_USER);
    }

    @DisplayName("Должен корректно обрабатывать null значения в параметрах")
    @Test
    void methodCallsWithNullParametersHandleGracefully() {
        assertDoesNotThrow(() -> userServiceTest.getUserByUsername(null));
        assertDoesNotThrow(() -> userServiceTest.userExists(null));

        verify(userRepositoryMock, times(1)).findByUsername(null);
        verify(userRepositoryMock, times(1)).existsByUsername(null);
    }
}