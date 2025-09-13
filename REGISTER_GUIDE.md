# Hướng dẫn sử dụng form đăng ký

## Tổng quan

Form đăng ký cho phép người dùng tạo tài khoản mới trong hệ thống quản lý trắc nghiệm. Giao diện được thiết kế đẹp mắt và thân thiện với người dùng.

## Cách truy cập

1. **Từ màn hình đăng nhập**: Nhấn nút "Chưa có tài khoản? Đăng ký"
2. **Chạy trực tiếp**: Sử dụng `RegisterDemo.java` để test form

## Các trường thông tin

### 1. Tên đăng nhập
- **Bắt buộc**: Có
- **Độ dài tối thiểu**: 3 ký tự
- **Yêu cầu**: Không được trùng với tài khoản đã có

### 2. Email
- **Bắt buộc**: Có
- **Định dạng**: Phải là email hợp lệ (có @ và domain)
- **Yêu cầu**: Không được trùng với email đã có

### 3. Mật khẩu
- **Bắt buộc**: Có
- **Độ dài tối thiểu**: 6 ký tự
- **Hiển thị**: Dấu * để bảo mật

### 4. Xác nhận mật khẩu
- **Bắt buộc**: Có
- **Yêu cầu**: Phải khớp với mật khẩu

### 5. Vai trò
- **Tùy chọn**: Teacher hoặc Student
- **Mặc định**: Student
- **Lưu ý**: Không thể chọn Admin (chỉ có thể tạo bởi Admin hiện có)

## Validation (Kiểm tra dữ liệu)

### Kiểm tra phía client:
- Tất cả trường bắt buộc phải được điền
- Tên đăng nhập tối thiểu 3 ký tự
- Mật khẩu tối thiểu 6 ký tự
- Mật khẩu xác nhận phải khớp
- Email phải đúng định dạng

### Kiểm tra phía server:
- Tên đăng nhập không được trùng
- Email không được trùng
- Lưu vào database thành công

## Giao diện người dùng

### Thiết kế:
- **Màu sắc**: Xanh dương chủ đạo, xanh lá cho thành công, đỏ cho lỗi
- **Font**: Segoe UI, dễ đọc
- **Layout**: GridBagLayout, căn chỉnh đẹp mắt
- **Responsive**: Tự động điều chỉnh kích thước

### Các nút:
- **Đăng ký**: Màu xanh lá, thực hiện đăng ký
- **Hủy**: Màu xám, đóng form
- **Đã có tài khoản? Đăng nhập**: Màu xanh dương, chuyển sang form đăng nhập

## Thông báo

### Thành công:
- Dialog xanh với icon ✓
- Thông báo "Đăng ký thành công!"
- Tự động chuyển về form đăng nhập

### Lỗi validation:
- Dialog vàng với icon ⚠
- Thông báo lỗi cụ thể
- Nút "Đã hiểu" để đóng

### Lỗi đăng ký:
- Dialog đỏ với icon ✗
- Thông báo "Tên đăng nhập hoặc email có thể đã tồn tại"
- Nút "Thử lại" để đóng

## Phím tắt

- **Enter**: Thực hiện đăng ký
- **Escape**: Đóng form (nếu có)

## Luồng xử lý

1. **Nhập thông tin** → Validation phía client
2. **Nhấn Đăng ký** → Hiển thị "Đang đăng ký..."
3. **Gửi request** → Kiểm tra phía server
4. **Thành công** → Hiển thị dialog thành công → Chuyển về đăng nhập
5. **Thất bại** → Hiển thị dialog lỗi → Cho phép thử lại

## Lưu ý kỹ thuật

- **Threading**: Đăng ký chạy trong thread riêng để không block UI
- **Database**: Sử dụng UserDAO để lưu thông tin
- **Security**: Mật khẩu lưu dạng plain text (nên mã hóa trong production)
- **Error handling**: Xử lý lỗi database và network

## Demo

Để test form đăng ký:

```bash
# Chạy demo
mvn exec:java -Dexec.mainClass="com.quiz.RegisterDemo"
```

Hoặc từ form đăng nhập chính:
```bash
# Chạy ứng dụng chính
mvn exec:java -Dexec.mainClass="com.quiz.Main"
# Nhấn "Chưa có tài khoản? Đăng ký"
```
