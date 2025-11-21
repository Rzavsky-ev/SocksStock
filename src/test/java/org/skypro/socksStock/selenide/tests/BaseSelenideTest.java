package org.skypro.socksStock.selenide.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый класс для Selenide тестов
 * Настраивает окружение и конфигурацию браузера для автоматизированного тестирования
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseSelenideTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String BROWSER = "chrome";
    private static final boolean HEADLESS = true;
    private static final int TIMEOUT = 10000;
    private static final String BROWSER_SIZE = "1920x1080";
    private static final boolean SCREENSHOTS = true;
    private static final boolean SAVE_PAGE_SOURCE = false;
    private static final String REPORTS_FOLDER = "target/selenide-screenshots";

    /**
     * Порт приложения, инжектируемый Spring Boot
     * Используется для построения полного URL тестируемого приложения
     */
    @LocalServerPort
    protected int port;

    /**
     * Метод инициализации, выполняемый один раз перед всеми тестами
     * Настраивает статическую конфигурацию Selenide и добавляет Allure listener
     */
    @BeforeAll
    static void beforeAll() {

        Configuration.browser = BROWSER;
        Configuration.headless = HEADLESS;

        Configuration.timeout = TIMEOUT;

        Configuration.browserSize = BROWSER_SIZE;

        Configuration.screenshots = SCREENSHOTS;
        Configuration.savePageSource = SAVE_PAGE_SOURCE;
        Configuration.reportsFolder = REPORTS_FOLDER;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    /**
     * Метод настройки, выполняемый перед каждым тестом
     * Устанавливает базовый URL на основе динамического порта приложения
     */
    @BeforeEach
    void setUp() {
        Configuration.baseUrl = BASE_URL + port;
    }

    /**
     * Метод очистки, выполняемый после каждого теста
     * Закрывает веб-драйвер и освобождает ресурсы
     */
    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
}