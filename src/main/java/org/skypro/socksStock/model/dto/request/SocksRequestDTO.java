package org.skypro.socksStock.model.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для запросов, связанных с операциями над носками.
 */
@Getter
@Setter
public class SocksRequestDTO {
    private String color;
    private Integer cottonPart;
    private Integer quantity;
}
