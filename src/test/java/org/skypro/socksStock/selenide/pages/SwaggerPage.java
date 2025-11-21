package org.skypro.socksStock.selenide.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object класс для работы со страницей Swagger UI
 * Предоставляет методы для взаимодействия с элементами Swagger-документации
 */
public class SwaggerPage {

    // Основные контейнеры и элементы управления
    private final SelenideElement swaggerContainer = $(".swagger-ui");
    private final SelenideElement authorizeButton = $(".btn.authorize");

    // Секции API
    private final SelenideElement authSection = $("#operations-tag-auth-controller");
    private final SelenideElement socksSection = $("#operations-tag-socks-stock-controller");
    private final SelenideElement adminSection = $("#operations-tag-user-admin-controller");

    // Эндпоинты секции аутентификации
    private final SelenideElement loginEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/auth/login'])");
    private final SelenideElement registerEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/auth/register'])");
    private final SelenideElement registerAdminEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/auth/register-admin'])");

    // Эндпоинты секции управления носками
    private final SelenideElement socksIncomeEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/socks/income'])");
    private final SelenideElement socksOutcomeEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/socks/outcome'])");
    private final SelenideElement socksSearchEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/socks'])");
    private final SelenideElement socksDeleteEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/socks/delete'])");

    // Эндпоинты секции администратора
    private final SelenideElement adminUsersEndpoint =
            $("div.opblock:has(.opblock-summary-path[data-path='/api/admin/users'])");

    /**
     * Открывает страницу Swagger UI
     *
     * @return текущий экземпляр SwaggerPage
     */
    public SwaggerPage open() {
        Selenide.open("/swagger-ui/index.html");
        swaggerContainer.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    /**
     * Раскрывает секцию аутентификации
     *
     * @return текущий экземпляр SwaggerPage
     */
    public SwaggerPage expandAuthSection() {
        if (authSection.exists() && !isSectionExpanded(authSection)) {
            authSection.click();
            authSection.shouldHave(attribute("data-is-open", "true"), Duration.ofSeconds(1));
        }
        return this;
    }

    /**
     * Раскрывает секцию управления носками
     *
     * @return текущий экземпляр SwaggerPage
     */
    public SwaggerPage expandSocksSection() {
        if (socksSection.exists() && !isSectionExpanded(socksSection)) {
            socksSection.click();
            socksSection.shouldHave(attribute("data-is-open", "true"), Duration.ofSeconds(3));
        }
        return this;
    }

    /**
     * Раскрывает секцию администратора
     *
     * @return текущий экземпляр SwaggerPage
     */
    public SwaggerPage expandAdminSection() {
        if (adminSection.exists() && !isSectionExpanded(adminSection)) {
            adminSection.click();
            adminSection.shouldHave(attribute("data-is-open", "true"), Duration.ofSeconds(3));
        }
        return this;
    }

    /**
     * Проверяет, раскрыта ли секция
     *
     * @param section элемент секции для проверки
     * @return true если секция раскрыта, false в противном случае
     */
    private boolean isSectionExpanded(SelenideElement section) {
        return "true".equals(section.getAttribute("data-is-open"));
    }

    // Методы проверки видимости секций

    /**
     * Проверяет видимость секции аутентификации
     *
     * @return true если секция видима, false в противном случае
     */
    public boolean isAuthSectionVisible() {
        return authSection.exists() && authSection.isDisplayed();
    }

    /**
     * Проверяет видимость секции управления носками
     *
     * @return true если секция видима, false в противном случае
     */
    public boolean isSocksSectionVisible() {
        return socksSection.exists() && socksSection.isDisplayed();
    }

    /**
     * Проверяет видимость секции администратора
     *
     * @return true если секция видима, false в противном случае
     */
    public boolean isAdminSectionVisible() {
        return adminSection.exists() && adminSection.isDisplayed();
    }

    // Методы проверки видимости эндпоинтов
    /**
     * Проверяет видимость эндпоинта входа в систему
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isLoginEndpointVisible() {
        return loginEndpoint.exists() && loginEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта регистрации пользователя
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isRegisterEndpointVisible() {
        return registerEndpoint.exists() && registerEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта регистрации администратора
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isRegisterAdminEndpointVisible() {
        return registerAdminEndpoint.exists() && registerAdminEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта прихода носков
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isSocksIncomeEndpointVisible() {
        return socksIncomeEndpoint.exists() && socksIncomeEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта расхода носков
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isSocksOutcomeEndpointVisible() {
        return socksOutcomeEndpoint.exists() && socksOutcomeEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта поиска носков
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isSocksSearchEndpointVisible() {
        return socksSearchEndpoint.exists() && socksSearchEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта удаления носков
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isSocksDeleteEndpointVisible() {
        return socksDeleteEndpoint.exists() && socksDeleteEndpoint.isDisplayed();
    }

    /**
     * Проверяет видимость эндпоинта управления пользователями
     *
     * @return true если эндпоинт видим, false в противном случае
     */
    public boolean isAdminUsersEndpointVisible() {
        return adminUsersEndpoint.exists() && adminUsersEndpoint.isDisplayed();
    }

    // Методы проверки состояния раскрытия секций

    /**
     * Проверяет, раскрыта ли секция аутентификации
     *
     * @return true если секция раскрыта, false в противном случае
     */
    public boolean isAuthSectionExpanded() {
        return isSectionExpanded(authSection);
    }

    /**
     * Проверяет, раскрыта ли секция управления носками
     *
     * @return true если секция раскрыта, false в противном случае
     */
    public boolean isSocksSectionExpanded() {
        return isSectionExpanded(socksSection);
    }

    /**
     * Проверяет, раскрыта ли секция администратора
     *
     * @return true если секция раскрыта, false в противном случае
     */
    public boolean isAdminSectionExpanded() {
        return isSectionExpanded(adminSection);
    }

    /**
     * Ожидает загрузки страницы Swagger
     *
     * @return текущий экземпляр SwaggerPage
     */
    public SwaggerPage waitForLoad() {
        swaggerContainer.shouldBe(visible, Duration.ofSeconds(10));
        $(".opblock-tag").shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    /**
     * Проверяет, загружена ли страница Swagger
     *
     * @return true если страница загружена, false в противном случае
     */
    public boolean isPageLoaded() {
        return swaggerContainer.isDisplayed() && authorizeButton.isDisplayed();
    }

    /**
     * Возвращает количество секций на странице
     *
     * @return количество секций API
     */
    public int getSectionCount() {
        return $$(".opblock-tag").size();
    }

    /**
     * Возвращает количество эндпоинтов на странице
     *
     * @return количество эндпоинтов API
     */
    public int getEndpointCount() {
        return $$(".opblock-summary").size();
    }

    /**
     * Раскрывает все секции API на странице
     */
    public void expandAllSections() {
        expandAuthSection();
        expandSocksSection();
        expandAdminSection();
    }
}
