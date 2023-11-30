package com.example.market.repository;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByStatus(ItemStatus status, Pageable pageable);

    @Query(value = "select i.*, ST_Distance_Sphere(u.location, :point) as dis " +
            "from item i join users u on i.user_id = u.user_id " +
            "having dis <= :scope", nativeQuery = true)
    Page<Item> customFindAllByDistance(Pageable pageable,
                                       @Param("point") Point point,
                                       @Param("scope") Double scope);
}
