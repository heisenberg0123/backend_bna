package com.example.reporting.controllers;

import com.example.reporting.entities.Report;
import com.example.reporting.repositories.ReportRepository;
import com.example.reporting.services.ReportSchedulingService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportSchedulingService reportSchedulingService;
    private final ReportRepository reportRepository;

    public ReportController(ReportSchedulingService reportSchedulingService, ReportRepository reportRepository) {
        this.reportSchedulingService = reportSchedulingService;
        this.reportRepository = reportRepository;
    }

    @PostMapping("/{id}/schedule")
    public Report scheduleExport(@PathVariable Long id, @RequestParam String scheduledDate, @RequestParam String format, @RequestParam(required = false) String filters) {
        LocalDateTime dateTime = LocalDateTime.parse(scheduledDate)
                .atZone(ZoneId.of("Europe/Paris"))
                .toLocalDateTime();
        Report report = reportSchedulingService.scheduleExport(id, dateTime, format);
        if (filters != null && !filters.isEmpty()) {
            report.setFilters(filters); // Save filters to the report
            reportRepository.save(report); // Assuming reportRepository is injected or accessible
        }
        return report;
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) throws MalformedURLException {
        String filePath = reportSchedulingService.getReportFilePath(id);
        if (filePath == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(filePath);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName().toString())
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}