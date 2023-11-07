package com.example.market.repository;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByStatus(ItemStatus status, Pageable pageable);
}
