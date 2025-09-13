-- Script sửa lỗi FOREIGN KEY constraint với Users
USE quanlytracnghiem;

-- ========================
-- 1. Kiểm tra dữ liệu hiện tại
-- ========================
PRINT '=== KIỂM TRA USERS ===';
SELECT id, username, email, roleId FROM Users ORDER BY id;

PRINT '=== KIỂM TRA ROLES ===';
SELECT * FROM Roles ORDER BY id;

-- ========================
-- 2. Xóa dữ liệu cũ nếu có
-- ========================
DELETE FROM UserAnswers;
DELETE FROM ExamResults;
DELETE FROM Exam_Questions;
DELETE FROM Exams;
DELETE FROM Question_CorrectAnswers;
DELETE FROM Questions;

-- ========================
-- 3. Đảm bảo có đủ users
-- ========================
-- Kiểm tra và thêm users nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'admin')
    INSERT INTO Users (username, password, email, roleId) 
    VALUES ('admin', 'admin123', 'admin@example.com', 0);

IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'teacher')
    INSERT INTO Users (username, password, email, roleId) 
    VALUES ('teacher', 'teacher123', 'teacher@example.com', 1);

IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'student')
    INSERT INTO Users (username, password, email, roleId) 
    VALUES ('student', 'student123', 'student@example.com', 2);

-- ========================
-- 4. Hiển thị kết quả
-- ========================
PRINT '=== USERS SAU KHI SỬA ===';
SELECT id, username, email, roleId FROM Users ORDER BY id;

PRINT 'Users foreign key issue fixed successfully!';
