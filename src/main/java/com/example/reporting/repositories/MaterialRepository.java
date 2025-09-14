package com.example.reporting.repositories;

import com.example.reporting.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material,Long> {

}
