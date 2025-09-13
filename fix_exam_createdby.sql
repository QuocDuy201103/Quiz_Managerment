-- Script sửa lỗi FOREIGN KEY constraint cho Exams.createdBy
USE quanlytracnghiem;

PRINT '=== FIXING EXAM CREATEDBY ISSUE ===';

-- 1. Kiểm tra và thêm roles nếu thiếu
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 0)
    INSERT INTO Roles (id, name) VALUES (0, 'admin');
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 1)
    INSERT INTO Roles (id, name) VALUES (1, 'teacher');
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 2)
    INSERT INTO Roles (id, name) VALUES (2, 'student');

-- 2. Kiểm tra và thêm users nếu thiếu
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

-- 3. Kiểm tra và sửa exams có createdBy không hợp lệ
PRINT 'Checking existing exams...';
SELECT e.id, e.title, e.createdBy, u.username 
FROM Exams e 
LEFT JOIN Users u ON e.createdBy = u.id 
WHERE u.id IS NULL;

-- Cập nhật exams có createdBy không hợp lệ thành admin (ID = 1)
UPDATE Exams 
SET createdBy = 1 
WHERE createdBy NOT IN (SELECT id FROM Users);

-- 4. Kiểm tra và sửa questions có createdBy không hợp lệ
PRINT 'Checking existing questions...';
SELECT q.id, q.content, q.createdBy, u.username 
FROM Questions q 
LEFT JOIN Users u ON q.createdBy = u.id 
WHERE u.id IS NULL;

-- Cập nhật questions có createdBy không hợp lệ thành admin (ID = 1)
UPDATE Questions 
SET createdBy = 1 
WHERE createdBy NOT IN (SELECT id FROM Users);

-- 5. Hiển thị kết quả cuối cùng
PRINT '=== FINAL RESULT ===';
PRINT 'USERS:';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;

PRINT 'EXAMS:';
SELECT e.id, e.title, e.createdBy, u.username as created_by_username
FROM Exams e 
LEFT JOIN Users u ON e.createdBy = u.id 
ORDER BY e.id;

PRINT 'QUESTIONS:';
SELECT q.id, LEFT(q.content, 50) as content_preview, q.createdBy, u.username as created_by_username
FROM Questions q 
LEFT JOIN Users u ON q.createdBy = u.id 
ORDER BY q.id;

PRINT 'Fix completed successfully!';
