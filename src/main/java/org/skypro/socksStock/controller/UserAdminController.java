package org.skypro.socksStock.controller;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административного управления пользователями.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    /**
     * Возвращает список всех зарегистрированных пользователей системы.
     *
     * @return ResponseEntity со списком всех пользователей
     */
    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity с найденным пользователем или 404 если пользователь не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Находит пользователя по его имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return ResponseEntity с найденным пользователем или 404 если пользователь не найден
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<AppUser> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     * @return ResponseEntity со статусом 200 OK при успешном удалении или 404 если пользователь не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}