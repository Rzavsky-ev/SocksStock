package org.skypro.socksStock.model.converter;

import org.mapstruct.Mapper;
import org.skypro.socksStock.model.dto.response.SocksResponseDTO;
import org.skypro.socksStock.model.entity.Socks;

/**
 * Конвертер для преобразования сущности носков в DTO ответа.
 */
@Mapper(componentModel = "spring")
public interface SocksEntityToDtoConverter {

    /**
     * Преобразует сущность Socks в объект SocksResponseDTO.
     *
     * @param entity сущность Socks, полученная из базы данных
     * @return объект SocksResponseDTO, готовый для возврата в HTTP-ответе
     */
    SocksResponseDTO toDto(Socks entity);
}