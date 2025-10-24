package org.skypro.socksStock.repository;

import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью AppUser в базе данных.
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Находит пользователя по имени пользователя (логину).
     *
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой Optional, если пользователь не найден
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Проверяет существование пользователя с указанным именем пользователя.
     *
     * @param username имя пользователя для проверки
     * @return true если пользователь с таким именем существует, false в противном случае
     */
    Boolean existsByUsername(String username);

    /**
     * Находит всех пользователей с указанной ролью.
     *
     * @param role роль пользователей для фильтрации
     * @return список пользователей с указанной ролью (может быть пустым)
     */
    List<AppUser> findByRole(Role role);
}