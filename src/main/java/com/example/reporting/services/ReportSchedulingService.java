package com.example.reporting.services;

import com.example.reporting.entities.Report;
import com.example.reporting.repositories.ReportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.example.reporting.entities.Report;
import com.example.reporting.repositories.ReportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportSchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(ReportSchedulingService.class);
    private final ReportRepository reportRepository;
    @Value("${file.storage.path:/tmp/reports}")
    private String storagePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportSchedulingService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report scheduleExport(Long reportId, LocalDateTime scheduledDate, String format) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        String scheduledExport = String.format("{\"dateTime\": \"%s\", \"format\": \"%s\", \"done\": false, \"filePath\": \"\"}",
                scheduledDate.toString(), format.toLowerCase());
        report.setScheduledExport(scheduledExport);
        logger.info("Scheduled export for report {} at {}", reportId, scheduledDate);
        return reportRepository.save(report);
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkScheduledExports() {
        logger.info("Checking scheduled exports at {}", LocalDateTime.now(ZoneId.of("Europe/Paris")));
        List<Report> reports = reportRepository.findAll();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        for (Report report : reports) {
            if (report.getScheduledExport() != null) {
                try {
                    JsonNode node = objectMapper.readTree(report.getScheduledExport());
                    LocalDateTime scheduledDate = LocalDateTime.parse(node.get("dateTime").asText());
                    boolean done = node.get("done").asBoolean();
                    if (scheduledDate.isBefore(now) && !done) {
                        logger.info("Processing report {} scheduled for {}", report.getId(), scheduledDate);
                        String filePath = exportReport(report, node.get("format").asText());
                        if (filePath != null) {
                            ObjectNode objectNode = (ObjectNode) node;
                            objectNode.put("done", true);
                            objectNode.put("filePath", filePath);
                            report.setScheduledExport(objectMapper.writeValueAsString(objectNode));
                            reportRepository.save(report);
                            logger.info("Updated report {} with filePath: {}", report.getId(), filePath);
                        } else {
                            logger.error("Failed to generate file for report {}", report.getId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error processing report {}: {}", report.getId(), e.getMessage(), e);
                }
            }
        }
    }

    private String exportReport(Report report, String format) {
        List<String[]> data = getReportData(report);
        if (data.isEmpty()) {
            logger.warn("No data for report {}", report.getId());
            return null;
        }
        data.add(0, getHeader(report.getEntityType()));

        String fileName = "report_" + report.getId() + "_" + LocalDateTime.now(ZoneId.of("Europe/Paris")).toString().replace(":", "-") + "." + format;
        Path filePath = Path.of(storagePath, fileName);
        try {
            Files.createDirectories(filePath.getParent());
            if ("csv".equals(format)) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
                    writer.writeAll(data);
                }
            } else if ("pdf".equals(format)) {
                PdfWriter writer = new PdfWriter(filePath.toFile());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.add(new Paragraph("Report ID: " + report.getId()));
                data.forEach(row -> document.add(new Paragraph(String.join(", ", row))));
                document.close();
            }
            logger.info("Generated file at {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            logger.error("IOException generating file for report {}: {}", report.getId(), e.getMessage(), e);
            return null;
        }
    }

    private List<String[]> getReportData(Report report) {
        String entityType = report.getEntityType();
        String filtersJson = report.getFilters();
        List<String[]> data = new ArrayList<>();
        List<Object[]> allData = new ArrayList<>();

        // Fetch all data based on entity type
        if ("employee".equals(entityType)) {
            allData = reportRepository.findAllEmployeesWithDetails();
        } else if ("material".equals(entityType)) {
            allData = reportRepository.findAllMaterialsWithDetails();
        } else if ("transaction".equals(entityType)) {
            allData = reportRepository.findAllTransactionsWithDetails();
        } else if ("supplier".equals(entityType)) {
            allData = reportRepository.findAllSuppliersWithDetails();
        }

        // Parse dynamic filters from the Report model
        List<DynamicFilter> dynamicFilters = parseDynamicFilters(filtersJson);
        data = filterData(allData, dynamicFilters, getFields(entityType));
        return data;
    }

    private List<DynamicFilter> parseDynamicFilters(String filtersJson) {
        try {
            if (filtersJson != null && !filtersJson.isEmpty()) {
                return objectMapper.readValue(filtersJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DynamicFilter.class));
            }
        } catch (Exception e) {
            logger.warn("Failed to parse filters for report: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<String[]> filterData(List<Object[]> allData, List<DynamicFilter> filters, String[] fields) {
        return allData.stream()
                .filter(row -> matchesDynamicFilters(row, filters, fields))
                .map(row -> {
                    String[] rowData = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        rowData[i] = (row.length > i) ? String.valueOf(row[i]) : "";
                    }
                    return rowData;
                })
                .collect(Collectors.toList());
    }

    private boolean matchesDynamicFilters(Object[] row, List<DynamicFilter> filters, String[] fields) {
        for (DynamicFilter filter : filters) {
            int index = List.of(fields).indexOf(filter.attribute);
            if (index >= 0 && row.length > index) {
                String value = String.valueOf(row[index]).toLowerCase();
                String filterValue = String.valueOf(filter.value).toLowerCase();
                if ("contains".equals(filter.type)) {
                    if (!value.contains(filterValue)) return false;
                } else if ("equals".equals(filter.type)) {
                    if (!value.equals(filterValue)) return false;
                } else if ("starts with".equals(filter.type)) {
                    if (!value.startsWith(filterValue)) return false;
                } else if ("ends with".equals(filter.type)) {
                    if (!value.endsWith(filterValue)) return false;
                } else if ("greater than".equals(filter.type)) {
                    try {
                        if (!(Double.parseDouble(value) > Double.parseDouble(filterValue))) return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else if ("less than".equals(filter.type)) {
                    try {
                        if (!(Double.parseDouble(value) < Double.parseDouble(filterValue))) return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else if ("between".equals(filter.type)) {
                    try {
                        double val = Double.parseDouble(value);
                        double min = Double.parseDouble(String.valueOf(filter.value)); // Cast Object to String
                        double max = filter.value2 != null ? Double.parseDouble(String.valueOf(filter.value2)) : Double.MAX_VALUE; // Handle null
                        if (!(val >= min && val <= max)) return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private String[] getFields(String entityType) {
        if ("employee".equals(entityType)) return new String[]{"id", "name", "department", "role"};
        if ("material".equals(entityType)) return new String[]{"id", "name", "quantity", "type"};
        if ("transaction".equals(entityType)) return new String[]{"quantity", "date", "type", "employee_id"};
        if ("supplier".equals(entityType)) return new String[]{"id", "name", "contact_person", "phone", "email"};
        return new String[]{"id", "data"};
    }

    private String[] getHeader(String entityType) {
        if ("employee".equals(entityType)) return new String[]{"ID", "Name", "Department", "Role"};
        if ("material".equals(entityType)) return new String[]{"ID", "Name", "Quantity", "Type"};
        if ("transaction".equals(entityType)) return new String[]{"Quantity", "Date", "Type", "Employee ID"};
        if ("supplier".equals(entityType)) return new String[]{"ID", "Name", "Contact Person", "Phone", "Email"};
        return new String[]{"ID", "Data"};
    }

    public String getReportFilePath(Long reportId) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report != null && report.getScheduledExport() != null) {
            try {
                JsonNode node = objectMapper.readTree(report.getScheduledExport());
                String filePath = node.get("filePath").asText();
                return filePath.isEmpty() ? null : filePath;
            } catch (Exception e) {
                logger.error("Error parsing filePath for report {}: {}", reportId, e.getMessage());
                return null;
            }
        }
        return null;
    }
}

// DynamicFilter class
class DynamicFilter {
    public String attribute;
    public String type;
    public Object value; // Can be String or Number
    public Object value2; // Optional, can be String, Number, or null

    // Default constructor for Jackson
    public DynamicFilter() {}
}