package org.skypro.socksStock.service;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.exception.UserNotFoundException;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями системы.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Возвращает список всех зарегистрированных пользователей системы.
     *
     * @return список всех пользователей
     */
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем или пустой Optional, если пользователь не найден
     */
    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Находит пользователя по его имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой Optional, если пользователь не найден
     */
    public Optional<AppUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Обновляет роль пользователя по его идентификатору.
     *
     * @param userId  идентификатор пользователя
     * @param newRole новая роль пользователя
     * @return обновленный объект пользователя
     * @throws UserNotFoundException если пользователь с указанным идентификатором не найден
     */
    public AppUser updateUserRole(Long userId, Role newRole) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId, HttpStatus.NOT_FOUND));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя для удаления
     * @throws UserNotFoundException если пользователь с указанным идентификатором не найден
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Проверяет существование пользователя с указанным именем.
     *
     * @param username имя пользователя для проверки
     * @return true если пользователь существует, false в противном случае
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Возвращает список пользователей с указанной ролью.
     *
     * @param role роль пользователей для фильтрации
     * @return список пользователей с указанной ролью
     */
    public List<AppUser> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}