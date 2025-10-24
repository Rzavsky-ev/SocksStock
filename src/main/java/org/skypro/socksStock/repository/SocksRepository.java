package org.skypro.socksStock.repository;

import org.skypro.socksStock.model.entity.Socks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Socks в базе данных.
 */
@Repository
public interface SocksRepository extends JpaRepository<Socks, Long> {

    /**
     * Находит носки по цвету и проценту содержания хлопка.
     *
     * @param color      цвет носков для поиска
     * @param cottonPart процент содержания хлопка для поиска
     * @return Optional с найденными носками или пустой Optional, если носки не найдены
     */
    Optional<Socks> findByColorAndCottonPart(String color, Integer cottonPart);

    /**
     * Вычисляет общее количество носков указанного цвета с содержанием хлопка больше заданного значения.
     *
     * @param color      цвет носков для фильтрации
     * @param cottonPart минимальный процент содержания хлопка (исключающий)
     * @return общее количество носков, удовлетворяющих условиям (0 если нет таких носков)
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Socks s WHERE s.color = :color AND s.cottonPart > :cottonPart")
    Integer sumQuantityByColorAndCottonPartGreaterThan(@Param("color") String color, @Param("cottonPart") Integer cottonPart);

    /**
     * Вычисляет общее количество носков указанного цвета с содержанием хлопка меньше заданного значения.
     *
     * @param color      цвет носков для фильтрации
     * @param cottonPart максимальный процент содержания хлопка (исключающий)
     * @return общее количество носков, удовлетворяющих условиям (0 если нет таких носков)
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Socks s WHERE s.color = :color AND s.cottonPart < :cottonPart")
    Integer sumQuantityByColorAndCottonPartLessThan(@Param("color") String color, @Param("cottonPart") Integer cottonPart);

    /**
     * Вычисляет общее количество носков указанного цвета с содержанием хлопка равным заданному значению.
     *
     * @param color      цвет носков для фильтрации
     * @param cottonPart точный процент содержания хлопка
     * @return общее количество носков, удовлетворяющих условиям (0 если нет таких носков)
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Socks s WHERE s.color = :color AND s.cottonPart = :cottonPart")
    Integer sumQuantityByColorAndCottonPartEqual(@Param("color") String color, @Param("cottonPart") Integer cottonPart);
}
