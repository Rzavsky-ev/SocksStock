package org.skypro.socksStock.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;


/**
 * Сервис для управления складом носков.
 */
@Service
@RequiredArgsConstructor
public class SocksStockService {

    private final SocksRepository socksRepository;
    private final SocksEntityToDtoConverter converterToDto;
    private final SocksDtoToEntityConverter converterToEntity;

    /**
     * Обрабатывает приход носков на склад.
     *
     * @param request DTO с данными о носках для добавления
     * @return SocksResponseDTO с информацией о добавленных носках
     * @throws EmptyDataException если данные запроса невалидны
     */
    public SocksResponseDTO incomeSocks(SocksRequestDTO request) {
        validateRequest(request);
        return socksRepository.findByColorAndCottonPart(request.getColor(), request.getCottonPart())
                .map(existingSocks -> addToStock(existingSocks, request))
                .orElseGet(() -> createSocks(request));
    }

    /**
     * Валидирует данные запроса операций с носками.
     *
     * @param request DTO запроса для валидации
     * @throws EmptyDataException если данные не проходят валидацию
     */
    private void validateRequest(SocksRequestDTO request) {
        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
            throw new EmptyDataException("Color is required and cannot be empty.", HttpStatus.BAD_REQUEST);
        }
        if (request.getCottonPart() == null || request.getCottonPart() < 0 || request.getCottonPart() > 100) {
            throw new EmptyDataException("CottonPart is required and must be between 0 and 100.", HttpStatus.BAD_REQUEST);
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new EmptyDataException("Quantity must be greater than 0.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Добавляет носки к существующей партии на складе.
     *
     * @param socks   существующая партия носков
     * @param request DTO с данными для добавления
     * @return SocksResponseDTO с обновленной информацией
     */
    private SocksResponseDTO addToStock(Socks socks, SocksRequestDTO request) {
        socks.setQuantity(socks.getQuantity() + request.getQuantity());
        Socks updatedSocks = socksRepository.save(socks);
        return converterToDto.toDto(updatedSocks);
    }

    /**
     * Создает новую партию носков на складе.
     *
     * @param request DTO с данными для создания
     * @return SocksResponseDTO с информацией о созданной партии
     */
    private SocksResponseDTO createSocks(SocksRequestDTO request) {
        Socks newSocks = converterToEntity.toEntity(request);
        Socks savedSocks = socksRepository.save(newSocks);
        return converterToDto.toDto(savedSocks);
    }

    /**
     * Обрабатывает расход носков со склада.
     *
     * @param request DTO с данными о носках для списания
     * @return SocksResponseDTO с информацией о списанных носках
     * @throws SocksNotFoundException если носки не найдены на складе
     */
    public SocksResponseDTO outcomeSocks(SocksRequestDTO request) {
        validateRequest(request);
        return socksRepository.findByColorAndCottonPart(request.getColor(), request.getCottonPart())
                .map(existingSocks -> removeFromStock(existingSocks, request))
                .orElseThrow(() ->
                        new SocksNotFoundException("These socks are out of stock.", HttpStatus.BAD_REQUEST));
    }

    /**
     * Уменьшает количество носков в существующей партии.
     *
     * @param socks   существующая партия носков
     * @param request DTO с данными для списания
     * @return SocksResponseDTO с обновленной информацией
     * @throws InvalidQuantityException если запрошенное количество превышает доступное
     */
    private SocksResponseDTO removeFromStock(Socks socks, SocksRequestDTO request) {
        if (socks.getQuantity() < request.getQuantity()) {
            throw new InvalidQuantityException("No socks found with color: " + request.getColor() +
                    " and cotton part: " + request.getCottonPart(), HttpStatus.BAD_REQUEST);
        }
        socks.setQuantity(socks.getQuantity() - request.getQuantity());
        Socks updatedSocks = socksRepository.save(socks);
        return converterToDto.toDto(updatedSocks);
    }

    /**
     * Возвращает общее количество носков, соответствующих критериям поиска.
     *
     * @param color      цвет носков для фильтрации
     * @param operation  операция сравнения для содержания хлопка
     * @param cottonPart значение содержания хлопка для сравнения
     * @return общее количество носков, удовлетворяющих критериям
     * @throws EmptyDataException если параметры запроса невалидны
     */
    public Integer getQuantity(String color, Operation operation, Integer cottonPart) {
        validateColorAndCottonPart(color, cottonPart);

        return switch (operation) {
            case moreThan -> socksRepository.sumQuantityByColorAndCottonPartGreaterThan(color, cottonPart);
            case lessThan -> socksRepository.sumQuantityByColorAndCottonPartLessThan(color, cottonPart);
            case equal -> socksRepository.sumQuantityByColorAndCottonPartEqual(color, cottonPart);
        };
    }

    /**
     * Валидирует параметры цвета и содержания хлопка для запросов поиска.
     *
     * @param color      цвет для валидации
     * @param cottonPart содержание хлопка для валидации
     * @throws EmptyDataException если параметры не проходят валидацию
     */
    private void validateColorAndCottonPart(String color, Integer cottonPart) {
        if (color == null || color.trim().isEmpty()) {
            throw new EmptyDataException("Color is required and cannot be empty.", HttpStatus.BAD_REQUEST);
        }
        if (cottonPart == null || cottonPart < 0 || cottonPart > 100) {
            throw new EmptyDataException("CottonPart must be between 0 and 100.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Удаляет все записи о носках из базы данных.
     */
    public void deleteAll() {
        socksRepository.deleteAll();
    }
}