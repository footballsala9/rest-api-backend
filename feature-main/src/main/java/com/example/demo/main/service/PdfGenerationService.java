package com.example.demo.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    public byte[] generateTaggedPdf() {
        try {
            // Load JRXML
            InputStream reportStream = getClass().getResourceAsStream("/reports/sample-tagged.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Report template not found: /reports/sample-tagged.jrxml");
            }

            // Compile Report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Fill Report (Empty DataSource for static text)
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // Export to PDF with Tagging
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setTagged(true); // Enable Tagged PDF
            configuration.setMetadataAuthor("Demo Application");
            configuration.setMetadataTitle("Sample Tagged Report");
            configuration.setDisplayMetadataTitle(true);
            // configuration.setPdfVersion(PdfVersion.VERSION_1_7); // Optional

            exporter.setConfiguration(configuration);
            exporter.exportReport();

            return baos.toByteArray();

        } catch (JRException e) {
            log.error("Failed to generate PDF with JasperReports", e);
            throw new RuntimeException("PDF Generation failed", e);
        }
    }
}
