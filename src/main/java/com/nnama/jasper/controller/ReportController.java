package com.nnama.jasper.controller;

import com.nnama.jasper.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
	private final ReportService reportService;

	@GetMapping("/{format}")
	public ResponseEntity<byte[]> export(@PathVariable("format") String format) {
		try {
				byte[] data = reportService.exportReport(format);
				String f = format == null ? "pdf" : format.toLowerCase();
				String filename;
				String contentType;
				boolean inline;

				switch (f) {
					case "html":
						filename = "employees.html";
						contentType = MediaType.TEXT_HTML_VALUE;
						inline = true;
						break;
					case "csv":
						filename = "employees.csv";
						contentType = "text/csv";
						inline = false;
						break;
					case "xls":
						filename = "employees.xls";
						contentType = "application/vnd.ms-excel";
						inline = false;
						break;
					case "xlsx":
						filename = "employees.xlsx";
						contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
						inline = false;
						break;
					case "docx":
						filename = "employees.docx";
						contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
						inline = false;
						break;
					case "pptx":
						filename = "employees.pptx";
						contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
						inline = false;
						break;
					case "pdf":
					default:
						filename = "employees.pdf";
						contentType = MediaType.APPLICATION_PDF_VALUE;
						inline = true;
				}

				String disposition = (inline ? "inline" : "attachment") + "; filename=" + filename;
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, disposition)
						.header(HttpHeaders.CONTENT_TYPE, contentType)
						.body(data);
		} catch (Exception e) {
			log.error("Failed to export report: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.TEXT_PLAIN)
					.body(("Export failed: " + e.getMessage()).getBytes());
		}
	}

		@GetMapping("/{name}/{format}")
		public ResponseEntity<byte[]> exportNamed(@PathVariable("name") String name,
																							@PathVariable("format") String format) {
			try {
				byte[] data = reportService.exportReport(name, format);
				String f = format == null ? "pdf" : format.toLowerCase();
				String filename = name + "." + f;
				String contentType;
				boolean inline;

				switch (f) {
					case "html":
						contentType = MediaType.TEXT_HTML_VALUE; inline = true; break;
					case "csv":
						contentType = "text/csv"; inline = false; break;
					case "xls":
						contentType = "application/vnd.ms-excel"; inline = false; break;
					case "xlsx":
						contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; inline = false; break;
					case "docx":
						contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; inline = false; break;
					case "pptx":
						contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"; inline = false; break;
					case "pdf":
					default:
						contentType = MediaType.APPLICATION_PDF_VALUE; inline = true; break;
				}

				String disposition = (inline ? "inline" : "attachment") + "; filename=" + filename;
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, disposition)
						.header(HttpHeaders.CONTENT_TYPE, contentType)
						.body(data);
			} catch (Exception e) {
				log.error("Failed to export report '{}': {}", name, e.getMessage(), e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.TEXT_PLAIN)
						.body(("Export failed: " + e.getMessage()).getBytes());
			}
		}

}
