package com.seoulotakus.takumapbe.domain.shop.repository;

import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
