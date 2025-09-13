# Hướng dẫn khắc phục lỗi database

## Lỗi thường gặp

### 1. Lỗi kết nối SQL Server

**Lỗi:**
```
The TCP/IP connection to the host localhost, port 1433 has failed. 
Error: "Connection refused: getsockopt"
```

**Nguyên nhân:**
- SQL Server chưa được cài đặt
- SQL Server chưa được khởi động
- Port 1433 bị chặn
- TCP/IP protocol chưa được bật

## Giải pháp

### Option 1: Cài đặt và cấu hình SQL Server

#### Bước 1: Tải và cài đặt SQL Server
```bash
# Tải SQL Server Express (miễn phí)
https://www.microsoft.com/en-us/sql-server/sql-server-downloads

# Hoặc SQL Server Developer Edition (miễn phí)
https://www.microsoft.com/en-us/sql-server/sql-server-downloads
```

#### Bước 2: Cấu hình SQL Server
1. **Mở SQL Server Configuration Manager**
2. **Bật TCP/IP Protocol:**
   - SQL Server Network Configuration → Protocols for SQLEXPRESS
   - Right-click TCP/IP → Enable
   - Restart SQL Server service

3. **Cấu hình TCP/IP:**
   - Double-click TCP/IP
   - Tab IP Addresses
   - IPAll → TCP Port: 1433
   - Restart SQL Server service

#### Bước 3: Kiểm tra SQL Server Service
```bash
# Mở Windows Services
services.msc

# Tìm "SQL Server (SQLEXPRESS)" hoặc "SQL Server (MSSQLSERVER)"
# Đảm bảo Status = Running
```

#### Bước 4: Test kết nối
```bash
# Mở Command Prompt
telnet localhost 1433

# Nếu kết nối thành công, sẽ thấy màn hình đen
```

#### Bước 5: Tạo database
```sql
-- Mở SQL Server Management Studio
-- Tạo database mới
CREATE DATABASE QuizManagement;

-- Chạy script database_schema.sql
```

### Option 2: Sử dụng H2 Database (Đơn giản hơn)

Nếu bạn muốn test nhanh mà không cần cài SQL Server:

#### Bước 1: Chạy H2 Demo
```bash
# Biên dịch với H2 dependency
mvn clean compile

# Chạy H2 demo
mvn exec:java -Dexec.mainClass="com.quiz.H2Demo"
```

#### Bước 2: Đăng nhập
- **Username**: admin
- **Password**: admin123

#### Ưu điểm của H2:
- ✅ Không cần cài đặt
- ✅ Tự động tạo database và bảng
- ✅ Có sẵn dữ liệu mẫu
- ✅ Chạy trong memory (nhanh)
- ✅ Hoàn toàn miễn phí

#### Nhược điểm của H2:
- ❌ Dữ liệu mất khi tắt ứng dụng
- ❌ Không phù hợp cho production
- ❌ Không có giao diện quản lý

### Option 3: Sử dụng MySQL (Alternative)

Nếu bạn có MySQL:

#### Bước 1: Cài đặt MySQL
```bash
# Tải MySQL Community Server
https://dev.mysql.com/downloads/mysql/

# Hoặc sử dụng XAMPP
https://www.apachefriends.org/
```

#### Bước 2: Tạo database
```sql
CREATE DATABASE QuizManagement;
USE QuizManagement;

-- Chạy script database_schema.sql (cần sửa syntax cho MySQL)
```

#### Bước 3: Cập nhật config
```properties
# config.properties
db.url=jdbc:mysql://localhost:3306/QuizManagement?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_mysql_password
```

## Kiểm tra kết nối

### Test SQL Server
```bash
# Kiểm tra port
netstat -an | findstr 1433

# Test kết nối
telnet localhost 1433
```

### Test H2
```bash
# Chạy H2 demo
mvn exec:java -Dexec.mainClass="com.quiz.H2Demo"

# Kiểm tra console output
# Sẽ thấy: "Kết nối H2 database thành công!"
```

## Lỗi khác

### Lỗi authentication
```
Login failed for user 'sa'
```
**Giải pháp:**
- Kiểm tra username/password trong config.properties
- Đảm bảo SQL Server authentication mode = Mixed
- Enable sa account

### Lỗi firewall
```
Connection timeout
```
**Giải pháp:**
- Tắt Windows Firewall tạm thời
- Hoặc thêm exception cho port 1433

### Lỗi SSL
```
SSL connection error
```
**Giải pháp:**
- Thêm `encrypt=false;trustServerCertificate=true` vào connection string

## Khuyến nghị

### Cho Development:
- ✅ Sử dụng H2 Database (nhanh, đơn giản)
- ✅ Chạy `H2Demo.java` để test

### Cho Production:
- ✅ Sử dụng SQL Server
- ✅ Cài đặt đầy đủ và cấu hình bảo mật
- ✅ Backup dữ liệu thường xuyên

## Liên hệ hỗ trợ

Nếu vẫn gặp lỗi, hãy:
1. Kiểm tra log chi tiết
2. Thử H2 demo trước
3. Cài đặt SQL Server theo hướng dẫn
4. Kiểm tra firewall và antivirus

