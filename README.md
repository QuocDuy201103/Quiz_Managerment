# Hệ thống quản lý trắc nghiệm

Hệ thống quản lý trắc nghiệm được phát triển bằng Java Swing với kết nối SQL Server, hỗ trợ đầy đủ các chức năng quản lý người dùng, câu hỏi, đề thi và kết quả thi.

## Tính năng chính

### 1. Quản lý người dùng
- Đăng nhập/đăng xuất
- Đăng ký tài khoản mới
- Phân quyền: Admin, Teacher, Student
- Quản lý thông tin người dùng

### 2. Quản lý nội dung
- **Môn học**: Quản lý các môn học
- **Chủ đề**: Quản lý chủ đề theo môn học
- **Câu hỏi**: Tạo, sửa, xóa câu hỏi trắc nghiệm với nhiều đáp án đúng
- **Đề thi**: Tạo đề thi từ các câu hỏi có sẵn

### 3. Thi trắc nghiệm
- Giao diện thi thân thiện với người dùng
- Đếm ngược thời gian
- Lưu tiến độ làm bài
- Tự động nộp bài khi hết thời gian

### 4. Xem kết quả
- Xem kết quả thi chi tiết
- Thống kê điểm số
- Đánh giá kết quả

## Công nghệ sử dụng

- **Java 11+**
- **Swing** - Giao diện người dùng
- **SQL Server** - Cơ sở dữ liệu
- **Maven** - Quản lý dự án
- **FlatLaf** - Look and Feel hiện đại

## Cài đặt và chạy

### 1. Yêu cầu hệ thống
- Java 11 hoặc cao hơn
- SQL Server 2016 hoặc cao hơn
- Maven 3.6+

### 2. Cài đặt cơ sở dữ liệu

1. Tạo database mới trong SQL Server:
```sql
CREATE DATABASE QuizManagement;
```

2. Chạy script SQL để tạo các bảng (xem file `database_schema.sql`)

3. Thêm dữ liệu mẫu:
```sql
-- Thêm roles
INSERT INTO Roles (id, name) VALUES (1, 'admin');
INSERT INTO Roles (id, name) VALUES (2, 'teacher');
INSERT INTO Roles (id, name) VALUES (3, 'student');

-- Thêm difficulties
INSERT INTO Difficulties (id, level) VALUES (1, 'Easy');
INSERT INTO Difficulties (id, level) VALUES (2, 'Medium');
INSERT INTO Difficulties (id, level) VALUES (3, 'Hard');

-- Thêm admin user (username: admin, password: admin123)
INSERT INTO Users (username, password, email, roleId) 
VALUES ('admin', 'admin123', 'admin@example.com', 1);
```

### 3. Cấu hình kết nối database

Sửa file `src/main/java/com/quiz/database/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QuizManagement;encrypt=false;trustServerCertificate=true";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

### 4. Biên dịch và chạy

```bash
# Biên dịch dự án
mvn clean compile

# Chạy ứng dụng
mvn exec:java -Dexec.mainClass="com.quiz.Main"

# Hoặc tạo JAR file
mvn clean package
java -jar target/quiz-management-system-1.0.0.jar
```

## Cấu trúc dự án

```
src/main/java/com/quiz/
├── Main.java                          # Class chính
├── database/
│   └── DatabaseConnection.java        # Quản lý kết nối DB
├── dao/                               # Data Access Objects
│   ├── UserDAO.java
│   ├── QuestionDAO.java
│   ├── ExamDAO.java
│   ├── ExamResultDAO.java
│   ├── SubjectDAO.java
│   └── TopicDAO.java
├── model/                             # Model classes
│   ├── User.java
│   ├── Question.java
│   ├── Exam.java
│   ├── ExamResult.java
│   ├── UserAnswer.java
│   ├── Subject.java
│   ├── Topic.java
│   ├── Role.java
│   └── Difficulty.java
└── ui/                                # Giao diện người dùng
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── ExamTakingFrame.java
    └── panels/
        ├── UserManagementPanel.java
        ├── QuestionManagementPanel.java
        ├── ExamManagementPanel.java
        ├── StudentExamPanel.java
        ├── ExamResultPanel.java
        └── MyExamResultPanel.java
```

## Hướng dẫn sử dụng

### 1. Đăng nhập/Đăng ký
- **Đăng nhập**: Sử dụng tài khoản admin (admin/admin123) hoặc tài khoản đã có
- **Đăng ký**: Nhấn nút "Chưa có tài khoản? Đăng ký" để tạo tài khoản mới
- **Phân quyền**: Chọn vai trò Teacher hoặc Student khi đăng ký

### 2. Quản lý nội dung (Admin/Teacher)
- **Môn học**: Thêm, sửa, xóa môn học
- **Chủ đề**: Thêm chủ đề cho từng môn học
- **Câu hỏi**: Tạo câu hỏi với 4 đáp án và chọn đáp án đúng
- **Đề thi**: Tạo đề thi từ các câu hỏi có sẵn

### 3. Thi trắc nghiệm (Student)
- Chọn đề thi từ danh sách
- Làm bài theo thời gian quy định
- Xem kết quả sau khi nộp bài

### 4. Xem kết quả
- **Admin/Teacher**: Xem tất cả kết quả thi
- **Student**: Xem kết quả thi của mình

## Lưu ý

1. **Bảo mật**: Mật khẩu được lưu dạng plain text, nên mã hóa trong môi trường production
2. **Kết nối DB**: Đảm bảo SQL Server đang chạy và có thể kết nối
3. **Quyền truy cập**: Cấu hình quyền truy cập database phù hợp
4. **Backup**: Thường xuyên backup dữ liệu quan trọng

## Phát triển thêm

- Thêm mã hóa mật khẩu
- Hỗ trợ nhiều loại câu hỏi (đúng/sai, điền khuyết)
- Thêm báo cáo thống kê
- Hỗ trợ import/export câu hỏi
- Thêm tính năng thi online
- Cải thiện giao diện người dùng

## Liên hệ

Nếu có vấn đề hoặc cần hỗ trợ, vui lòng tạo issue trong repository này.
