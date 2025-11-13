package org.skypro.socksStock.integration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Интеграционные тесты контроллера управления складом носков")
public class SocksStockControllerIntegrationTest extends BaseApiTest {

    private static final String SOCKS_PATH = "/socks";
    private static final String SOCKS_OUTCOME_PATH = SOCKS_PATH + "/outcome";
    private static final String SOCKS_DELETE_PATH = SOCKS_PATH + "/delete";
    private static final String SOCKS_INCOME_PATH = SOCKS_PATH + "/income";

    @Test
    @DisplayName("Приход носков с валидными данными должен возвращать статус CREATED")
    void incomeSocks_WhenValidRequest_ShouldReturnCreated() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(201)
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(80))
                .body("quantity", equalTo(10));

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(80))
                .body("quantity", equalTo(20));
    }

    @Test
    @DisplayName("Приход носков с отрицательным количеством должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenNegativeQuantity_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": -10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Quantity must be greater than 0."));
    }

    @Test
    @DisplayName("Приход носков с null количеством должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenNullQuantity_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": null
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Quantity must be greater than 0."));
    }

    @Test
    @DisplayName("Приход носков с нулевым количеством должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenEqualToZeroQuantity_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 0
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Quantity must be greater than 0."));
    }

    @Test
    @DisplayName("Приход носков с количеством 1 должен возвращать статус CREATED")
    void incomeSocks_WhenEqualToOneQuantity_ShouldReturnCreated() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 1
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(80))
                .body("quantity", equalTo(1));
    }

    @Test
    @DisplayName("Приход носков с большим количеством должен возвращать статус CREATED")
    void incomeSocks_WhenLargeQuantity_ShouldReturnCreated() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10000000
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(80))
                .body("quantity", equalTo(10000000));
    }

    @Test
    @DisplayName("Приход носков с отрицательным процентом хлопка должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenNegativeCottonPart_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": -80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("CottonPart is required and must be between 0 and 100."));
    }

    @Test
    @DisplayName("Приход носков с null процентом хлопка должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenNullCottonPart_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": null,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("CottonPart is required and must be between 0 and 100."));
    }

    @Test
    @DisplayName("Приход носков с процентом хлопка больше 100 должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenCottonPartIsMoreThan100_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 120,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("CottonPart is required and must be between 0 and 100."));
    }

    @Test
    @DisplayName("Приход носков с нулевым процентом хлопка должен возвращать статус CREATED")
    void incomeSocks_WhenEqualToZeroCottonPart_ShouldReturnCreated() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 0,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(0))
                .body("quantity", equalTo(10));
    }

    @Test
    @DisplayName("Приход носков со 100% хлопка должен возвращать статус CREATED")
    void incomeSocks_WhenEqualToOneHundredCottonPart_ShouldReturnCreated() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 100,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(100))
                .body("quantity", equalTo(10));
    }

    @Test
    @DisplayName("Приход носков с null цветом должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenNullColor_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": null,
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Color is required and cannot be empty."));
    }

    @Test
    @DisplayName("Приход носков с пустым цветом должен возвращать статус BAD_REQUEST")
    void incomeSocks_WhenEmptyColor_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": "",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Color is required and cannot be empty."));
    }

    @Test
    @DisplayName("Приход носков с цветом содержащим пробелы должен возвращать статус CREATED")
    void incomeSocks_WhenColorWithSpaces_ShouldReturnBadRequest() {
        String requestBody = """
                {
                    "color": " red ",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Приход носков без аутентификации должен возвращать статус UNAUTHORIZED")
    void incomeSocks_WithoutAuth_ShouldReturnUnauthorized() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(specJsonBuilder().build())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Приход носков с невалидным токеном должен возвращать статус FORBIDDEN")
    void incomeSocks_WithInvalidToken_ShouldReturnForbidden() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(specJsonBuilder()
                        .addHeader("Authorization", "Bearer invalid_token_123")
                        .build())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Расход носков с валидными данными должен возвращать статус OK")
    void outcomeSocks_WhenValidRequest_ShouldReturnOk() {
        String socksOutcome = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        String socksIncome = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 30
                }
                """;

        given()
                .spec(getSpecJson())
                .body(socksIncome)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .spec(getSpecJson())
                .body(socksOutcome)
                .when()
                .post(SOCKS_OUTCOME_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("color", equalTo("red"))
                .body("cottonPart", equalTo(80))
                .body("quantity", equalTo(20));
    }

    @Test
    @DisplayName("Расход носков при отсутствии на складе должен возвращать статус BAD_REQUEST")
    void outcomeSocks_WhenNoSocksInStock_ShouldReturnBadRequest() {
        String socksOutcome = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(socksOutcome)
                .when()
                .post(SOCKS_OUTCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("These socks are out of stock."));
    }

    @Test
    @DisplayName("Расход носков больше чем приход должен возвращать статус BAD_REQUEST")
    void outcomeSocks_WhenOutcomeMoreIncome_ShouldReturnBadRequest() {
        String socksOutcome = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 30
                }
                """;

        String socksIncome = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(socksIncome)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .spec(getSpecJson())
                .body(socksOutcome)
                .when()
                .post(SOCKS_OUTCOME_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("No socks found with color: red and cotton part: 80"));
    }

    @Test
    @DisplayName("Получение количества носков при наличии на складе должно возвращать статус OK")
    void getQuantity_WhenThereAreSocksInStock_ShouldReturnOk() {
        String requestBody = """
                {
                    "color": "red",
                    "cottonPart": 80,
                    "quantity": 10
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "moreThan")
                .param("cottonPart", 50)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("10"));

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "lessThan")
                .param("cottonPart", 50)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "equal")
                .param("cottonPart", 80)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("10"));
    }

    @Test
    @DisplayName("Получение количества носков при отсутствии на складе должно возвращать статус OK")
    void getQuantity_WhenThereAreSocksNoInStock_ShouldReturnOk() {

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "moreThan")
                .param("cottonPart", 50)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "lessThan")
                .param("cottonPart", 50)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "equal")
                .param("cottonPart", 80)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));
    }

    @Test
    @DisplayName("Удаление всех носков при их наличии должно очищать склад")
    void allDelete_WhenSocksExist_ShouldDeleteAllSocks() {
        String requestBody1 = """
                {
                    "color": "red",
                    "cottonPart": 70,
                    "quantity": 10
                }
                """;

        String requestBody2 = """
                {
                    "color": "blue",
                    "cottonPart": 60,
                    "quantity": 50
                }
                """;

        given()
                .spec(getSpecJson())
                .body(requestBody1)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .spec(getSpecJson())
                .body(requestBody2)
                .when()
                .post(SOCKS_INCOME_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "equal")
                .param("cottonPart", 70)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("10"));

        given()
                .spec(getSpecParam())
                .param("color", "blue")
                .param("operation", "equal")
                .param("cottonPart", 60)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("50"));

        given()
                .spec(getSpecParam())
                .when()
                .delete(SOCKS_DELETE_PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .spec(getSpecParam())
                .param("color", "red")
                .param("operation", "equal")
                .param("cottonPart", 70)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));

        given()
                .spec(getSpecParam())
                .param("color", "blue")
                .param("operation", "equal")
                .param("cottonPart", 60)
                .when()
                .get(SOCKS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("0"));
    }
}