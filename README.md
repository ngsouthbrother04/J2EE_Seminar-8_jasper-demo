# Spring Boot Jasper Reports Demo

Một dự án mini dùng để demo tích hợp JasperReports trong Spring Boot: đọc dữ liệu qua Spring Data JPA, fill report từ JRXML và xuất ra nhiều định dạng (PDF, HTML, CSV, XLS, XLSX, DOCX, PPTX).

## Công nghệ sử dụng
- Java 17, Spring Boot 3.3.x (Web, Data JPA)
- JasperReports 6.21.x, OpenPDF (xuất PDF), Apache POI (XLS/XLSX/DOCX/PPTX)
- H2 in-memory database (mặc định, tự tạo schema và seed dữ liệu)

## Yêu cầu môi trường
- JDK 17
- Maven 3.9+

## Chạy trên IntelliJ IDEA
1) Mở dự án
- IntelliJ IDEA → "New Project from Existing Sources" → chọn file `pom.xml` của dự án → "Open as Project" và Trust project.
- Chờ IntelliJ tải dependencies Maven xong (xem ở góc phải dưới hoặc tab Maven).

2) Cấu hình JDK 17
- File → Project Structure → Project → Project SDK: chọn JDK 17.
- Nếu chưa có, Add SDK và trỏ tới JDK 17 trên máy (macOS: `/usr/libexec/java_home -V` để xem các phiên bản).

3) Bật Lombok và Annotation Processing
- Settings → Plugins → Marketplace: cài plugin "Lombok" (nếu chưa có).
- Settings → Build, Execution, Deployment → Compiler → Annotation Processors → tích "Enable annotation processing".

4) Chạy ứng dụng Spring Boot
- Mở lớp `SpringBootJasperReportApplication` → nhấn biểu tượng Run (mũi tên xanh ở gutter) hoặc Run → Run 'SpringBootJasperReportApplication'.
- Quan sát console: khi Tomcat khởi động với context-path `/jasper`, ứng dụng đã sẵn sàng.

5) Thử các endpoint (context-path: `/jasper`)
- Employees (mặc định):
	- PDF: http://localhost:8080/jasper/report/pdf
	- HTML: http://localhost:8080/jasper/report/html

- Chọn báo cáo theo tên + định dạng:
	- Employees: http://localhost:8080/jasper/report/employees/pdf
	- Departments: http://localhost:8080/jasper/report/departments/xlsx
	- Projects: http://localhost:8080/jasper/report/projects/csv

H2 Console: http://localhost:8080/jasper/h2-console  
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo_jasper;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

## Dữ liệu mẫu (seed)
- Departments: 5 phòng ban (Engineering, QA, HR, Sales, Finance)
- Projects: 5 dự án (Apollo, Zephyr, Orion, Helios, Hyperion)
- Employees: 100 nhân viên ngẫu nhiên (tên, chức danh, lương, ngày vào, phòng ban)

## Cấu trúc chính
```
src/
	main/
		java/com/nnama/jasper/
			SpringBootJasperReportApplication.java
					config/
						DataLoader.java         # seed dữ liệu
			controller/
				ReportController.java   # /report/{format} và /report/{name}/{format}
			entity/
				Employee.java
				Department.java
				Project.java
			repository/
				EmployeeRepository.java
				DepartmentRepository.java
				ProjectRepository.java
			service/
				ReportService.java      # compile/fill/export JasperReports
		resources/
			application.yaml          # H2 in-memory, context-path /jasper
			employees.jrxml           # thêm cột Department
			departments.jrxml
			projects.jrxml
```

---

Tham khảo JasperSoft Studio (tùy chọn để thiết kế JRXML):  
https://community.jaspersoft.com/project/jaspersoft-studio/releases
