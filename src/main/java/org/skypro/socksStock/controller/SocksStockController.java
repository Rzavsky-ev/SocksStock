package org.skypro.socksStock.controller;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.model.dto.request.Operation;
import org.skypro.socksStock.model.dto.request.SocksRequestDTO;
import org.skypro.socksStock.model.dto.response.SocksResponseDTO;
import org.skypro.socksStock.service.SocksStockService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления складом носков.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/socks")
public class SocksStockController {

    private final SocksStockService socksStockService;

    /**
     * Обрабатывает приход носков на склад.
     * Добавляет указанное количество носков с заданными характеристиками.
     *
     * @param request DTO-объект с данными о носках для добавления
     * @return SocksResponseDTO с информацией о добавленных носках
     */
    @PostMapping("/income")
    @ResponseStatus(HttpStatus.CREATED)
    public SocksResponseDTO incomeSocks(@RequestBody SocksRequestDTO request) {
        return socksStockService.incomeSocks(request);
    }

    /**
     * Обрабатывает расход носков со склада.
     * Списание указанного количества носков с заданными характеристиками.
     *
     * @param request DTO-объект с данными о носках для списания
     * @return SocksResponseDTO с информацией о списанных носках
     */
    @PostMapping("/outcome")
    @ResponseStatus(HttpStatus.OK)
    public SocksResponseDTO outcomeSocks(@RequestBody SocksRequestDTO request) {
        return socksStockService.outcomeSocks(request);
    }

    /**
     * Возвращает общее количество носков на складе, соответствующих критериям поиска.
     *
     * @param color      цвет носков для фильтрации
     * @param operation  операция сравнения (moreThan, lessThan, equal)
     * @param cottonPart процент содержания хлопка для сравнения
     * @return общее количество носков, соответствующих критериям
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Integer getQuantity(
            @RequestParam String color,
            @RequestParam Operation operation,
            @RequestParam Integer cottonPart) {
        return socksStockService.getQuantity(color, operation, cottonPart);
    }

    /**
     * Удаляет все записи о носках из системы.
     */
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void allDelete() {
        socksStockService.deleteAll();
    }
}