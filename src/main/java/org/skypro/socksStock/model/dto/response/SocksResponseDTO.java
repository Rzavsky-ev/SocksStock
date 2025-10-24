package org.skypro.socksStock.model.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для ответа, содержащего информацию о носках.
 */
@Setter
@Getter
public class SocksResponseDTO {

    private String color;
    private Integer cottonPart;
    private Integer quantity;
}
