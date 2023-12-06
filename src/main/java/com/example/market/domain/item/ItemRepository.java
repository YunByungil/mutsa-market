package com.example.market.domain.item;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByStatusIn(List<ItemStatus> statuses, Pageable pageable);

    @Query(value = "select i.*, ST_Distance_Sphere(u.location, :point) as dis " +
            "from item i join users u on i.user_id = u.user_id " +
            "having dis <= :scope", nativeQuery = true)
    Page<Item> customFindAllByDistance(Pageable pageable,
                                       @Param("point") Point point,
                                       @Param("scope") Double scope);


    Page<Item> findAllByStatusInAndUserId(List<ItemStatus> statuses, Pageable pageable,
                                          @Param("userId") Long userId);

    Page<Item> findAllByStatusAndUserId(ItemStatus status, Pageable pageable,
                                        @Param("userId") Long userId);
}
