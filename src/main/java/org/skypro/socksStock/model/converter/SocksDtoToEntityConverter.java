package org.skypro.socksStock.model.converter;

import org.mapstruct.Mapper;
import org.skypro.socksStock.model.dto.request.SocksRequestDTO;
import org.skypro.socksStock.model.entity.Socks;


/**
 * Конвертер для преобразования между DTO и сущностью носков.
 */
@Mapper(componentModel = "spring")
public interface SocksDtoToEntityConverter {

    /**
     * Преобразует объект SocksRequestDTO в сущность Socks.
     *
     * @param dto объект DTO с данными о носках, полученный из запроса
     * @return сущность Socks, готовая для сохранения в базе данных
     */
    Socks toEntity(SocksRequestDTO dto);
}