package org.skypro.socksStock.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private CustomUserDetailsService userDetailsServiceTest;

    @DisplayName("Должен успешно загрузить пользователя по имени пользователя")
    @Test
    void loadUserByUsernameSuccessfully() {
        String username = "testUser";
        String password = "testPassword";
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(Role.ROLE_USER);

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(appUser));

        UserDetails userDetails = userDetailsServiceTest.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        verify(userRepositoryMock, times(1)).findByUsername(username);
    }

    @DisplayName("Должен загрузить пользователя с ролью ADMIN")
    @Test
    void loadUserWithAdminRole() {
        String username = "adminUser";
        String password = "adminPassword";
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(Role.ROLE_ADMIN);

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(appUser));

        UserDetails userDetails = userDetailsServiceTest.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepositoryMock, times(1)).findByUsername(username);
    }

    @DisplayName("Должен выбросить исключение когда пользователь не найден")
    @Test
    void throwExceptionWhenUserNotFound() {
        String username = "nonExistentUser";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsServiceTest.loadUserByUsername(username)
        );

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepositoryMock, times(1)).findByUsername(username);
    }

    @DisplayName("Должен корректно создать UserDetails с правильными authorities")
    @Test
    void createUserDetailsWithCorrectAuthorities() {
        String username = "testUser";
        String password = "testPassword";
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(Role.ROLE_USER);

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(appUser));

        UserDetails userDetails = userDetailsServiceTest.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(1, userDetails.getAuthorities().size());

        var authority = userDetails.getAuthorities().iterator().next();
        assertEquals("ROLE_USER", authority.getAuthority());
        assertInstanceOf(SimpleGrantedAuthority.class, authority);

        verify(userRepositoryMock, times(1)).findByUsername(username);
    }

    @DisplayName("Должен корректно обработать пользователя с пустым именем")
    @Test
    void handleUserWithEmptyUsername() {
        String username = "";

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsServiceTest.loadUserByUsername(username)
        );

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepositoryMock, times(1)).findByUsername(username);
    }

    @DisplayName("Должен корректно обработать пользователя с null именем")
    @Test
    void handleUserWithNullUsername() {
        String username = null;

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsServiceTest.loadUserByUsername(username)
        );

        assertEquals("User not found: null", exception.getMessage());
        verify(userRepositoryMock, times(1)).findByUsername(username);
    }
}