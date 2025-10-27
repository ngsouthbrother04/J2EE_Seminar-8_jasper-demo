package com.nnama.jasper.service;

import com.nnama.jasper.entity.Department;
import com.nnama.jasper.entity.Employee;
import com.nnama.jasper.entity.Project;
import com.nnama.jasper.repository.DepartmentRepository;
import com.nnama.jasper.repository.EmployeeRepository;
import com.nnama.jasper.repository.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService {

  EmployeeRepository repository;
  DepartmentRepository departmentRepository;
  ProjectRepository projectRepository;

  public byte[] exportReport(String reportFormat) throws JRException {
    String format = StringUtils.hasText(reportFormat) ? reportFormat.toLowerCase() : "pdf";

    // default: employees report
    return exportReport("employees", format);
  }

  public byte[] exportReport(String reportName, String reportFormat) throws JRException {
    String format = StringUtils.hasText(reportFormat) ? reportFormat.toLowerCase() : "pdf";

    // Load data based on report name
    JRBeanCollectionDataSource dataSource;
    String templateName;
    switch (reportName.toLowerCase()) {
      case "departments": {
        List<Department> items = departmentRepository.findAll();
        dataSource = new JRBeanCollectionDataSource(items);
        templateName = "/departments.jrxml";
        break;
      }
      case "projects": {
        List<Project> items = projectRepository.findAll();
        dataSource = new JRBeanCollectionDataSource(items);
        templateName = "/projects.jrxml";
        break;
      }
      case "employees":
      default: {
        List<Employee> items = repository.findAll();
        dataSource = new JRBeanCollectionDataSource(items);
        templateName = "/employees.jrxml";
      }
    }

    // Load and compile JRXML from classpath
    InputStream template = getClass().getResourceAsStream(templateName);
    if (template == null) {
      throw new JRException("Report template not found: " + templateName);
    }
    JasperReport jasperReport = JasperCompileManager.compileReport(template);

    Map<String, Object> params = new HashMap<>();
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

    if ("html".equals(format)) {
      HtmlExporter exporter = new HtmlExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      StringWriter writer = new StringWriter();
      exporter.setExporterOutput(new SimpleHtmlExporterOutput(writer));
      exporter.exportReport();
      return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    if ("csv".equals(format)) {
      // Exclude non-tabular bands like title from CSV output
      jasperPrint.setProperty("net.sf.jasperreports.export.csv.exclude.origin.band.1", "title");

      JRCsvExporter exporter = new JRCsvExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      StringWriter writer = new StringWriter();
      exporter.setExporterOutput(new SimpleWriterExporterOutput(writer));

      // Configure CSV to be Excel-friendly across locales
      SimpleCsvExporterConfiguration exporterCfg = new SimpleCsvExporterConfiguration();
      exporterCfg.setFieldDelimiter(";"); // use semicolon to avoid comma conflicts in many locales
      exporterCfg.setWriteBOM(Boolean.TRUE); // help Excel detect UTF-8
      exporter.setConfiguration(exporterCfg);

  exporterCfg.setRecordDelimiter("\r\n"); // Windows-friendly newlines

      exporter.exportReport();
      return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    if ("xls".equals(format)) {
      JRXlsExporter exporter = new JRXlsExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
      SimpleXlsReportConfiguration config = new SimpleXlsReportConfiguration();
      config.setOnePagePerSheet(false);
      config.setDetectCellType(true);
      config.setCollapseRowSpan(false);
      exporter.setConfiguration(config);
      exporter.exportReport();
      return baos.toByteArray();
    }

    if ("xlsx".equals(format)) {
      JRXlsxExporter exporter = new JRXlsxExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
      SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
      config.setDetectCellType(true);
      config.setCollapseRowSpan(false);
      exporter.setConfiguration(config);
      exporter.exportReport();
      return baos.toByteArray();
    }

    if ("docx".equals(format)) {
      JRDocxExporter exporter = new JRDocxExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
      exporter.exportReport();
      return baos.toByteArray();
    }

    if ("pptx".equals(format)) {
      JRPptxExporter exporter = new JRPptxExporter();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
      exporter.exportReport();
      return baos.toByteArray();
    }

    // default: PDF
    return JasperExportManager.exportReportToPdf(jasperPrint);
  }
}
