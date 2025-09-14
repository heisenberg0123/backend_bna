package com.example.reporting.repositories;


import com.example.reporting.entities.SupplyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {
}