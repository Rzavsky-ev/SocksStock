package org.skypro.socksStock.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность, представляющая носки на складе.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "socks")
public class Socks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "color")
    private String color;

    @Column(nullable = false, name = "cotton_part")
    private int cottonPart;

    @Column(nullable = false, name = "quantity")
    private int quantity;
}
