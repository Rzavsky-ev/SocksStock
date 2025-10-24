package org.skypro.socksStock.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @InjectMocks
    private DataInitializer dataInitializerTest;

    @DisplayName("Должен создать администратора, когда пользователь не существует")
    @Test
    void createAdminWhenUserDoesNotExist() {
        String username = "admin";
        String rawPassword = "admin";
        String encodedPassword = "encodedPassword";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializerTest.run();

        verify(userRepositoryMock, times(1)).findByUsername(username);
        verify(passwordEncoderMock, times(1)).encode(rawPassword);
        verify(userRepositoryMock, times(1)).save(any(AppUser.class));

        verify(userRepositoryMock).save(argThat(user ->
                user.getUsername().equals(username) &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getRole() == Role.ROLE_ADMIN
        ));
    }

    @DisplayName("Не должен создавать администратора, когда пользователь уже существует")
    @Test
    void notCreateAdminWhenUserAlreadyExists() {
        String username = "admin";
        AppUser existingAdmin = new AppUser();
        existingAdmin.setUsername(username);
        existingAdmin.setPassword("existingPassword");
        existingAdmin.setRole(Role.ROLE_ADMIN);

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(existingAdmin));

        dataInitializerTest.run();

        verify(userRepositoryMock, times(1)).findByUsername(username);
        verify(passwordEncoderMock, never()).encode(anyString());
        verify(userRepositoryMock, never()).save(any(AppUser.class));
    }

    @DisplayName("Должен использовать PasswordEncoder для кодирования пароля")
    @Test
    void usePasswordEncoderForPasswordEncoding() {
        String username = "admin";
        String rawPassword = "admin";
        String encodedPassword = "encodedAdminPassword123";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializerTest.run();

        verify(passwordEncoderMock, times(1)).encode(rawPassword);
        verify(userRepositoryMock).save(argThat(user ->
                user.getPassword().equals(encodedPassword)
        ));
    }

    @DisplayName("Должен установить правильную роль ROLE_ADMIN для создаваемого пользователя")
    @Test
    void setCorrectRoleForCreatedUser() {
        String username = "admin";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(anyString())).thenReturn("encodedPassword");
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        dataInitializerTest.run();

        verify(userRepositoryMock).save(argThat(user ->
                user.getRole() == Role.ROLE_ADMIN
        ));
    }

    @DisplayName("Должен корректно обработать пустые аргументы командной строки")
    @Test
    void handleEmptyCommandLineArguments() {
        String username = "admin";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(anyString())).thenReturn("encodedPassword");
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        dataInitializerTest.run();

        verify(userRepositoryMock, times(1)).findByUsername(username);
        verify(userRepositoryMock, times(1)).save(any(AppUser.class));
    }

    @DisplayName("Должен корректно обработать аргументы командной строки")
    @Test
    void handleCommandLineArguments() {
        String username = "admin";
        String[] args = {"arg1", "arg2"};

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(anyString())).thenReturn("encodedPassword");
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        dataInitializerTest.run(args);

        verify(userRepositoryMock, times(1)).findByUsername(username);
        verify(userRepositoryMock, times(1)).save(any(AppUser.class));
    }

    @DisplayName("Должен создать пользователя с правильными данными")
    @Test
    void createUserWithCorrectData() {
        String username = "admin";
        String rawPassword = "admin";
        String encodedPassword = "encodedPassword";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializerTest.run();

        verify(userRepositoryMock).save(argThat(user ->
                user.getUsername().equals(username) && user.getPassword().equals(encodedPassword) &&
                        user.getRole() == Role.ROLE_ADMIN
        ));
    }
}
