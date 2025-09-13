-- Script sửa lỗi FOREIGN KEY constraint cho ExamResults.userId
USE quanlytracnghiem;

PRINT '=== FIXING EXAMRESULT USERID ISSUE ===';

-- 1. Kiểm tra users hiện tại
PRINT 'Current users:';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;

-- 2. Kiểm tra ExamResults có userId không hợp lệ
PRINT 'ExamResults with invalid userId:';
SELECT er.id, er.userId, er.examId, er.score, u.username
FROM ExamResults er 
LEFT JOIN Users u ON er.userId = u.id 
WHERE u.id IS NULL;

-- 3. Thêm users mẫu nếu thiếu
-- Admin user (ID = 1)
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 1)
BEGIN
    PRINT 'Adding admin user with ID = 1...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (1, 'admin', 'admin123', 'admin@example.com', 0);
END

-- Teacher user (ID = 2) 
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 2)
BEGIN
    PRINT 'Adding teacher user with ID = 2...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (2, 'teacher', 'teacher123', 'teacher@example.com', 1);
END

-- Student user (ID = 3)
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 3)
BEGIN
    PRINT 'Adding student user with ID = 3...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (3, 'student', 'student123', 'student@example.com', 2);
END

-- Student user (ID = 4)
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 4)
BEGIN
    PRINT 'Adding student user with ID = 4...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (4, 'hocsinh1', 'hocsinh123', 'hocsinh1@example.com', 2);
END

-- Student user (ID = 5)
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 5)
BEGIN
    PRINT 'Adding student user with ID = 5...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (5, 'hocsinh2', 'hocsinh123', 'hocsinh2@example.com', 2);
END

-- 4. Sửa ExamResults có userId không hợp lệ
PRINT 'Fixing ExamResults with invalid userId...';
UPDATE ExamResults 
SET userId = 1 
WHERE userId NOT IN (SELECT id FROM Users);

-- 5. Hiển thị kết quả cuối cùng
PRINT '=== FINAL RESULT ===';
PRINT 'USERS:';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;

PRINT 'EXAMRESULTS:';
SELECT er.id, er.userId, u.username, er.examId, er.score, er.submittedAt
FROM ExamResults er 
LEFT JOIN Users u ON er.userId = u.id 
ORDER BY er.id;

PRINT 'Fix completed successfully!';
