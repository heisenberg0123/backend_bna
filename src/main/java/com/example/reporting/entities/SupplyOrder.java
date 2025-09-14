package com.example.reporting.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
public class SupplyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
    private String order_date;
    private int quantity_ordered;
    private String status;
    private double total_cost;

    public SupplyOrder() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getQuantity_ordered() {
        return quantity_ordered;
    }

    public void setQuantity_ordered(int quantity_ordered) {
        this.quantity_ordered = quantity_ordered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public SupplyOrder(Long id, Supplier supplier, Material material, String order_date, int quantity_ordered, String status, double total_cost) {
        this.id = id;
        this.supplier = supplier;
        this.material = material;
        this.order_date = order_date;
        this.quantity_ordered = quantity_ordered;
        this.status = status;
        this.total_cost = total_cost;
    }

    @Override
    public String toString() {
        return "SupplyOrder{" +
                "id=" + id +
                ", supplier=" + supplier +
                ", material=" + material +
                ", order_date='" + order_date + '\'' +
                ", quantity_ordered=" + quantity_ordered +
                ", status='" + status + '\'' +
                ", total_cost=" + total_cost +
                '}';
    }
}
