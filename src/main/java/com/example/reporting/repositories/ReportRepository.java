package com.example.reporting.repositories;

import com.example.reporting.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT id, name, department, role FROM employee", nativeQuery = true)
    List<Object[]> findAllEmployeesWithDetails();

    @Query(value = "SELECT id, name, quantity, type FROM material", nativeQuery = true)
    List<Object[]> findAllMaterialsWithDetails();

    @Query(value = "SELECT id, name, contact_person, phone, email FROM supplier", nativeQuery = true)
    List<Object[]> findAllSuppliersWithDetails();

    @Query(value = "SELECT t.quantity, t.date, t.type, e.id AS employee_id FROM transaction t JOIN employee e ON t.employee_id = e.id", nativeQuery = true)
    List<Object[]> findAllTransactionsWithDetails();
}