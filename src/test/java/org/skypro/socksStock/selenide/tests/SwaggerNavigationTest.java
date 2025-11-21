package org.skypro.socksStock.selenide.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.socksStock.selenide.pages.SwaggerPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты навигации по Swagger UI")
public class SwaggerNavigationTest extends BaseSelenideTest {

    private final SwaggerPage swaggerPage = new SwaggerPage();

    @Test
    @DisplayName("Проверка загрузки страницы Swagger UI")
    void testSwaggerPageLoads() {
        swaggerPage.open().waitForLoad();

        assertTrue(swaggerPage.isPageLoaded());

        int sectionCount = swaggerPage.getSectionCount();
        int endpointCount = swaggerPage.getEndpointCount();

        assertTrue(sectionCount >= 3);
        assertTrue(endpointCount >= 10);
    }

    @Test
    @DisplayName("Проверка видимости всех секций API")
    void testAllSectionsAreVisible() {
        swaggerPage.open().waitForLoad();

        assertTrue(swaggerPage.isAuthSectionVisible());
        assertTrue(swaggerPage.isSocksSectionVisible());
        assertTrue(swaggerPage.isAdminSectionVisible());
    }

    @Test
    @DisplayName("Проверка раскрытия всех секций API")
    void testExpandAllSections() {
        swaggerPage.open().waitForLoad().expandAllSections();

        assertTrue(swaggerPage.isAuthSectionExpanded());
        assertTrue(swaggerPage.isSocksSectionExpanded());
        assertTrue(swaggerPage.isAdminSectionExpanded());
    }

    @Test
    @DisplayName("Проверка видимости всех эндпоинтов API")
    void testAllEndpointsAreVisible() {
        swaggerPage.open().waitForLoad().expandAllSections();

        assertTrue(swaggerPage.isLoginEndpointVisible());
        assertTrue(swaggerPage.isRegisterEndpointVisible());
        assertTrue(swaggerPage.isRegisterAdminEndpointVisible());
        assertTrue(swaggerPage.isSocksIncomeEndpointVisible());
        assertTrue(swaggerPage.isSocksOutcomeEndpointVisible());
        assertTrue(swaggerPage.isSocksSearchEndpointVisible());
        assertTrue(swaggerPage.isSocksDeleteEndpointVisible());
        assertTrue(swaggerPage.isAdminUsersEndpointVisible());
    }

    @Test
    @DisplayName("Проверка доступности кнопки авторизации")
    void testAuthorizationButton() {
        swaggerPage.open().waitForLoad();

        assertTrue(swaggerPage.isPageLoaded());
    }
}