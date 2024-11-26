# PTIT CHECK-IN

## Dự án website hỗ trợ điểm danh

### Phiên bản
- v3.1 - 25/11/2024

### Ngôn ngữ, công cụ & công nghệ sử dụng
- Ngôn ngữ: Java, Javascript, HTML, CSS, SQL
- Framework BE: Spring Framework (mô hình MVC) kết hợp Spring Boot
- Framework FE (CSS): Bootstrap 5, Tailwind
- Database: MySQL, Flyway (Quản lý phiên bản database)
- Template engine: Thymeleaf
- Quản lý dự án: Git, Github, Maven (build project)
- Design pattern: DI (Dependency Injection)
- IDE, công cụ: MySQL Workbench, Intellij, VSCode
- Khác: Websocket, Session

### Cài đặt
- B1: Cài JDK 17
- B2: Cài MySQL => tạo 1 schema mới
- B3: Trong file cấu hình src/main/resources/application-dev.yml, thay đổi 3 tham số sau:

  - url: jdbc:mysql://localhost:3306/[tên schema vừa tạo]
  - username: [tên tài khoản truy cập mysql]
  - password: [mật khẩu truy cập mysql]
- B4: Run