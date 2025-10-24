package org.skypro.socksStock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.exception.EmptyDataException;
import org.skypro.socksStock.exception.InvalidQuantityException;
import org.skypro.socksStock.exception.SocksNotFoundException;
import org.skypro.socksStock.model.converter.SocksDtoToEntityConverter;
import org.skypro.socksStock.model.converter.SocksEntityToDtoConverter;
import org.skypro.socksStock.model.dto.request.Operation;
import org.skypro.socksStock.model.dto.request.SocksRequestDTO;
import org.skypro.socksStock.model.dto.response.SocksResponseDTO;
import org.skypro.socksStock.model.entity.Socks;
import org.skypro.socksStock.repository.SocksRepository;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SocksStockServiceTest {

    @Mock
    private SocksRepository socksRepositoryMock;

    @Mock
    private SocksEntityToDtoConverter converterToDtoMock;

    @Mock
    private SocksDtoToEntityConverter converterToEntityMock;

    @InjectMocks
    private SocksStockService socksStockServiceTest;

    @DisplayName("Приход носков: когда носки существуют, должно добавить количество к существующим")
    @Test
    void incomeSocksWhenSocksExistShouldAddQuantityToExisting() {
        SocksRequestDTO request = createSocksRequestDTO("red", 80, 50);
        Socks existingSocks = createSocks(1L, "red", 80, 100);
        Socks updatedSocks = createSocks(1L, "red", 80, 150);
        SocksResponseDTO expectedResponse = createSocksResponseDTO("red", 80, 150);

        when(socksRepositoryMock.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.of(existingSocks));
        when(socksRepositoryMock.save(existingSocks)).thenReturn(updatedSocks);
        when(converterToDtoMock.toDto(updatedSocks)).thenReturn(expectedResponse);

        SocksResponseDTO response = socksStockServiceTest.incomeSocks(request);

        assertNotNull(response);
        assertEquals(150, response.getQuantity());
        assertEquals("red", response.getColor());
        assertEquals(80, response.getCottonPart());

        verify(socksRepositoryMock).findByColorAndCottonPart("red", 80);
        verify(socksRepositoryMock).save(existingSocks);
        verify(converterToDtoMock).toDto(updatedSocks);
        verify(converterToEntityMock, never()).toEntity(any());
    }

    @DisplayName("Приход носков: когда носки не существуют, должно создать новые носки")
    @Test
    void incomeSocksWhenSocksNotExistCreateNewSocks() {
        SocksRequestDTO request = createSocksRequestDTO("blue", 60, 30);
        Socks newSocks = createSocks(1L, "blue", 60, 30);
        Socks savedSocks = createSocks(2L, "blue", 60, 30);
        SocksResponseDTO expectedResponse = createSocksResponseDTO("blue", 60, 30);

        when(socksRepositoryMock.findByColorAndCottonPart("blue", 60))
                .thenReturn(Optional.empty());
        when(converterToEntityMock.toEntity(request)).thenReturn(newSocks);
        when(socksRepositoryMock.save(newSocks)).thenReturn(savedSocks);
        when(converterToDtoMock.toDto(savedSocks)).thenReturn(expectedResponse);

        SocksResponseDTO actualResponse = socksStockServiceTest.incomeSocks(request);

        assertNotNull(actualResponse);
        assertEquals(30, actualResponse.getQuantity());
        assertEquals("blue", actualResponse.getColor());
        assertEquals(60, actualResponse.getCottonPart());

        verify(socksRepositoryMock).findByColorAndCottonPart("blue", 60);
        verify(converterToEntityMock).toEntity(request);
        verify(socksRepositoryMock).save(newSocks);
        verify(converterToDtoMock).toDto(savedSocks);
    }

    @DisplayName("Приход носков: когда цвет пустой, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenColorIsEmptyThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO("", 80, 10);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("Color is required and cannot be empty.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
        verify(socksRepositoryMock, never()).save(any());
    }

    @DisplayName("Приход носков: когда цвет равен null, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenColorIsNullThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO(null, 50, 10);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("Color is required and cannot be empty.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
    }

    @DisplayName("Приход носков: когда содержание хлопка больше 100, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenCottonPartGreaterThan100ThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO("red", 101, 100);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("CottonPart is required and must be between 0 and 100.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
    }

    @DisplayName("Приход носков: когда содержание хлопка равно null, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenCottonPartIsNullThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO("red", null, 10);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("CottonPart is required and must be between 0 and 100.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
    }

    @DisplayName("Приход носков: когда количество отрицательное, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenQuantityIsNegativeThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO("red", 80, -1);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("Quantity must be greater than 0.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
    }

    @DisplayName("Приход носков: когда количество равно null, должно выбросить EmptyDataException")
    @Test
    void incomeSocksWhenQuantityIsNullThrowEmptyDataException() {
        SocksRequestDTO request = createSocksRequestDTO("red", 80, null);

        EmptyDataException exception = assertThrows(EmptyDataException.class,
                () -> socksStockServiceTest.incomeSocks(request));

        assertEquals("Quantity must be greater than 0.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock, never()).findByColorAndCottonPart(any(), any());
    }

    @DisplayName("Расход носков: когда носки существуют и количество достаточное, должно уменьшить количество")
    @Test
    void outcomeSocksWhenSocksExistAndQuantityIsSufficientDecreaseQuantity() {
        String color = "red";
        Integer cottonPart = 80;
        Integer initialQuantity = 100;
        Integer outcomeQuantity = 30;
        Integer expectedQuantity = 70;

        SocksRequestDTO request = createSocksRequestDTO(color, cottonPart, outcomeQuantity);
        Socks existingSocks = createSocks(1L, color, cottonPart, initialQuantity);
        Socks updatedSocks = createSocks(1L, color, cottonPart, expectedQuantity);
        SocksResponseDTO expectedResponse = createSocksResponseDTO(color, cottonPart, expectedQuantity);

        when(socksRepositoryMock.findByColorAndCottonPart(color, cottonPart))
                .thenReturn(Optional.of(existingSocks));
        when(socksRepositoryMock.save(any(Socks.class))).thenReturn(updatedSocks);
        when(converterToDtoMock.toDto(updatedSocks)).thenReturn(expectedResponse);

        SocksResponseDTO actualResponse = socksStockServiceTest.outcomeSocks(request);

        assertNotNull(actualResponse);
        assertEquals(expectedQuantity, actualResponse.getQuantity());
        assertEquals(color, actualResponse.getColor());
        assertEquals(cottonPart, actualResponse.getCottonPart());

        verify(socksRepositoryMock).findByColorAndCottonPart(color, cottonPart);
        verify(socksRepositoryMock).save(existingSocks);
        verify(converterToDtoMock).toDto(updatedSocks);
    }

    @DisplayName("Расход носков: когда количество недостаточное, должно выбросить InvalidQuantityException")
    @Test
    void outcomeSocksWhenQuantityInsufficientThrowInvalidQuantityException() {
        String color = "blue";
        Integer cottonPart = 50;
        Integer initialQuantity = 10;
        Integer outcomeQuantity = 20;

        SocksRequestDTO request = createSocksRequestDTO(color, cottonPart, outcomeQuantity);
        Socks existingSocks = createSocks(1L, color, cottonPart, initialQuantity);

        when(socksRepositoryMock.findByColorAndCottonPart(color, cottonPart))
                .thenReturn(Optional.of(existingSocks));

        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class,
                () -> socksStockServiceTest.outcomeSocks(request));

        assertEquals("No socks found with color: " + color + " and cotton part: " + cottonPart,
                exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock).findByColorAndCottonPart(color, cottonPart);
        verify(socksRepositoryMock, never()).save(any(Socks.class));
    }

    @DisplayName("Расход носков: когда списываются все носки, должно установить количество в 0")
    @Test
    void outcomeSocksWhenOutcomeAllSocksSetQuantityToZero() {
        String color = "green";
        Integer cottonPart = 70;
        Integer initialQuantity = 50;
        Integer outcomeQuantity = 50;
        Integer expectedQuantity = 0;

        SocksRequestDTO request = createSocksRequestDTO(color, cottonPart, outcomeQuantity);
        Socks existingSocks = createSocks(1L, color, cottonPart, initialQuantity);
        Socks updatedSocks = createSocks(1L, color, cottonPart, expectedQuantity);
        SocksResponseDTO expectedResponse = createSocksResponseDTO(color, cottonPart, expectedQuantity);

        when(socksRepositoryMock.findByColorAndCottonPart(color, cottonPart))
                .thenReturn(Optional.of(existingSocks));
        when(socksRepositoryMock.save(any(Socks.class))).thenReturn(updatedSocks);
        when(converterToDtoMock.toDto(updatedSocks)).thenReturn(expectedResponse);

        SocksResponseDTO actualResponse = socksStockServiceTest.outcomeSocks(request);

        assertNotNull(actualResponse);
        assertEquals(expectedQuantity, actualResponse.getQuantity());
        verify(socksRepositoryMock).save(existingSocks);
    }

    @DisplayName("Расход носков: когда носки не найдены, должно выбросить SocksNotFoundException")
    @Test
    void outcomeSocksWhenSocksNotFoundThrowSocksNotFoundException() {
        String color = "yellow";
        Integer cottonPart = 90;
        Integer quantity = 10;

        SocksRequestDTO request = createSocksRequestDTO(color, cottonPart, quantity);

        when(socksRepositoryMock.findByColorAndCottonPart(color, cottonPart))
                .thenReturn(Optional.empty());

        SocksNotFoundException exception = assertThrows(SocksNotFoundException.class,
                () -> socksStockServiceTest.outcomeSocks(request));

        assertEquals("These socks are out of stock.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(socksRepositoryMock).findByColorAndCottonPart(color, cottonPart);
        verify(socksRepositoryMock, never()).save(any(Socks.class));
    }

    @DisplayName("Получение количества: операция 'moreThan' должна вернуть корректную сумму")
    @Test
    void getQuantityWithMoreThanOperationReturnCorrectSum() {
        String color = "red";
        Operation operation = Operation.moreThan;
        Integer cottonPart = 50;
        Integer expectedSum = 150;

        when(socksRepositoryMock.sumQuantityByColorAndCottonPartGreaterThan(color, cottonPart))
                .thenReturn(expectedSum);

        Integer actualSum = socksStockServiceTest.getQuantity(color, operation, cottonPart);

        assertEquals(expectedSum, actualSum);
        verify(socksRepositoryMock, times(1))
                .sumQuantityByColorAndCottonPartGreaterThan(color, cottonPart);
    }

    @DisplayName("Получение количества: операция 'lessThan' должна вернуть корректную сумму")
    @Test
    void getQuantityWithLessThanOperationReturnCorrectSum() {
        String color = "blue";
        Operation operation = Operation.lessThan;
        Integer cottonPart = 80;
        Integer expectedSum = 75;

        when(socksRepositoryMock.sumQuantityByColorAndCottonPartLessThan(color, cottonPart))
                .thenReturn(expectedSum);

        Integer actualSum = socksStockServiceTest.getQuantity(color, operation, cottonPart);

        assertEquals(expectedSum, actualSum);
        verify(socksRepositoryMock, times(1))
                .sumQuantityByColorAndCottonPartLessThan(color, cottonPart);
    }

    @DisplayName("Получение количества: операция 'equal' должна вернуть корректную сумму")
    @Test
    void getQuantityWithEqualOperationReturnCorrectSum() {
        String color = "black";
        Operation operation = Operation.equal;
        Integer cottonPart = 100;
        Integer expectedSum = 200;

        when(socksRepositoryMock.sumQuantityByColorAndCottonPartEqual(color, cottonPart))
                .thenReturn(expectedSum);

        Integer actualSum = socksStockServiceTest.getQuantity(color, operation, cottonPart);

        assertEquals(expectedSum, actualSum);
        verify(socksRepositoryMock, times(1))
                .sumQuantityByColorAndCottonPartEqual(color, cottonPart);
    }

    @DisplayName("Получение количества: когда репозиторий возвращает null, должно вернуть null")
    @Test
    void getQuantityWhenRepositoryReturnsNullReturnNull() {
        String color = "purple";
        Operation operation = Operation.moreThan;
        Integer cottonPart = 30;

        when(socksRepositoryMock.sumQuantityByColorAndCottonPartGreaterThan(color, cottonPart))
                .thenReturn(null);

        Integer result = socksStockServiceTest.getQuantity(color, operation, cottonPart);

        assertNull(result);
        verify(socksRepositoryMock, times(1))
                .sumQuantityByColorAndCottonPartGreaterThan(color, cottonPart);
    }

    @DisplayName("Получение количества: когда носки не найдены, должно вернуть 0")
    @Test
    void getQuantityWhenNoSocksFoundReturnZero() {
        String color = "orange";
        Operation operation = Operation.lessThan;
        Integer cottonPart = 20;
        Integer expectedSum = 0;

        when(socksRepositoryMock.sumQuantityByColorAndCottonPartLessThan(color, cottonPart))
                .thenReturn(expectedSum);

        Integer actualSum = socksStockServiceTest.getQuantity(color, operation, cottonPart);

        assertEquals(expectedSum, actualSum);
        verify(socksRepositoryMock, times(1))
                .sumQuantityByColorAndCottonPartLessThan(color, cottonPart);
    }

    @DisplayName("Удаление всех носков из пустой базы данных")
    @Test
    void deleteAllWhenDatabaseEmptyCallRepository() {
        socksStockServiceTest.deleteAll();

        verify(socksRepositoryMock, times(1)).deleteAll();
        verifyNoMoreInteractions(socksRepositoryMock);
    }

    @DisplayName("Удаление всех носков из непустой базы данных")
    @Test
    void deleteAllWhenDatabaseHasDataCallRepository() {
        socksStockServiceTest.deleteAll();

        verify(socksRepositoryMock, times(1)).deleteAll();
    }

    private SocksRequestDTO createSocksRequestDTO(String color, Integer cottonPart, Integer quantity) {
        SocksRequestDTO request = new SocksRequestDTO();
        request.setColor(color);
        request.setCottonPart(cottonPart);
        request.setQuantity(quantity);
        return request;
    }

    private Socks createSocks(Long id, String color, Integer cottonPart, Integer quantity) {
        Socks socks = new Socks();
        socks.setId(id);
        socks.setColor(color);
        socks.setCottonPart(cottonPart);
        socks.setQuantity(quantity);
        return socks;
    }

    private SocksResponseDTO createSocksResponseDTO(String color, Integer cottonPart, Integer quantity) {
        SocksResponseDTO response = new SocksResponseDTO();

        response.setColor(color);
        response.setCottonPart(cottonPart);
        response.setQuantity(quantity);
        return response;
    }
}
