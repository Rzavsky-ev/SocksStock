package org.skypro.socksStock.config;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Класс для инициализации начальных данных в приложении.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод, выполняемый при запуске приложения.
     * Проверяет наличие пользователя с именем "admin" в базе данных и создает его,
     * если он не существует.
     *
     * @param args аргументы командной строки, переданные при запуске приложения
     */
    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=admin");
        }
    }
}