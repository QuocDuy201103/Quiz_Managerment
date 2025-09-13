# Hướng dẫn chạy nhanh

## 1. Chuẩn bị môi trường

### Yêu cầu:
- Java 11+
- SQL Server 2016+
- Maven 3.6+

## 2. Cài đặt database

1. **Tạo database:**
```sql
CREATE DATABASE QuizManagement;
```

2. **Chạy script tạo bảng:**
```bash
# Mở SQL Server Management Studio và chạy file database_schema.sql
```

3. **Kiểm tra dữ liệu mẫu đã được thêm:**
- 3 roles: admin, teacher, student
- 3 users mẫu: admin/admin123, teacher/teacher123, student/student123
- 3 môn học: Toán, Vật lý, Hóa học
- Một số câu hỏi và đề thi mẫu

## 3. Cấu hình kết nối

Sửa file `src/main/resources/config.properties`:
```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=QuizManagement;encrypt=false;trustServerCertificate=true
db.username=your_username
db.password=your_password
```

## 4. Chạy ứng dụng

```bash
# Biên dịch
mvn clean compile

# Chạy
mvn exec:java -Dexec.mainClass="com.quiz.Main"
```

## 5. Đăng nhập/Đăng ký

### Tài khoản có sẵn:
- **Admin**: admin/admin123
- **Teacher**: teacher/teacher123  
- **Student**: student/student123

### Đăng ký tài khoản mới:
1. Nhấn "Chưa có tài khoản? Đăng ký" trên màn hình đăng nhập
2. Điền thông tin: tên đăng nhập, email, mật khẩu
3. Chọn vai trò: Teacher hoặc Student
4. Nhấn "Đăng ký"

## 6. Sử dụng

### Admin:
- Quản lý người dùng
- Quản lý môn học, chủ đề
- Quản lý câu hỏi, đề thi
- Xem tất cả kết quả thi

### Teacher:
- Quản lý câu hỏi, đề thi
- Xem kết quả thi

### Student:
- Xem danh sách đề thi
- Làm bài thi
- Xem kết quả của mình

## 7. Tạo JAR file (tùy chọn)

```bash
mvn clean package
java -jar target/quiz-management-system-1.0.0.jar
```

## Lưu ý

- Đảm bảo SQL Server đang chạy
- Kiểm tra port 1433 có mở không
- Nếu gặp lỗi kết nối, kiểm tra lại thông tin trong config.properties
