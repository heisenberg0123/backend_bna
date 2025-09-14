package com.example.reporting.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private String entityType;
    @Column(columnDefinition = "TEXT")
    private String filters; // store as JSON string
    private String groupBy;
    private String chartType;
    private LocalDateTime createdAt;



    private String scheduledExport;
    // JSON: {"dateTime": "YYYY-MM-DDTHH:mm", "format": "csv" or "pdf", "done": false}

    public Report() {

    }
    public String getScheduledExport() {
        return scheduledExport;
    }

    public void setScheduledExport(String scheduledExport) {
        this.scheduledExport = scheduledExport;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Report(Long id, String name, String entityType, String filters, String groupBy, String chartType, LocalDateTime createdAt, String scheduledExport) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.filters = filters;
        this.groupBy = groupBy;
        this.chartType = chartType;
        this.createdAt = createdAt;
        this.scheduledExport = scheduledExport;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", entityType='" + entityType + '\'' +
                ", filters='" + filters + '\'' +
                ", groupBy='" + groupBy + '\'' +
                ", chartType='" + chartType + '\'' +
                ", createdAt=" + createdAt +
                ", scheduledExport='" + scheduledExport + '\'' +
                '}';
    }
}
