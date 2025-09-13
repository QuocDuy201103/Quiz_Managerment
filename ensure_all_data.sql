-- Script đảm bảo tất cả dữ liệu cần thiết có trong database
USE quanlytracnghiem;

-- ========================
-- 1. ROLES
-- ========================
PRINT '=== ENSURING ROLES ===';
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 0)
    INSERT INTO Roles (id, name) VALUES (0, 'admin');
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 1)
    INSERT INTO Roles (id, name) VALUES (1, 'teacher');
IF NOT EXISTS (SELECT 1 FROM Roles WHERE id = 2)
    INSERT INTO Roles (id, name) VALUES (2, 'student');

-- ========================
-- 2. DIFFICULTIES
-- ========================
PRINT '=== ENSURING DIFFICULTIES ===';
IF NOT EXISTS (SELECT 1 FROM Difficulties WHERE id = 0)
    INSERT INTO Difficulties (id, level) VALUES (0, 'Easy');
IF NOT EXISTS (SELECT 1 FROM Difficulties WHERE id = 1)
    INSERT INTO Difficulties (id, level) VALUES (1, 'Medium');
IF NOT EXISTS (SELECT 1 FROM Difficulties WHERE id = 2)
    INSERT INTO Difficulties (id, level) VALUES (2, 'Hard');

-- ========================
-- 3. SUBJECTS (nếu chưa có)
-- ========================
PRINT '=== ENSURING SUBJECTS ===';
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Toán học')
    INSERT INTO Subjects (name, description) VALUES (N'Toán học', N'Môn học về số học, đại số, hình học');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Vật lý')
    INSERT INTO Subjects (name, description) VALUES (N'Vật lý', N'Môn học về các hiện tượng tự nhiên');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Hóa học')
    INSERT INTO Subjects (name, description) VALUES (N'Hóa học', N'Môn học về cấu tạo và tính chất của chất');

-- ========================
-- 4. TOPICS (nếu chưa có)
-- ========================
PRINT '=== ENSURING TOPICS ===';
-- Lấy subject IDs
DECLARE @mathId INT, @physicsId INT, @chemistryId INT;
SELECT @mathId = id FROM Subjects WHERE name = N'Toán học';
SELECT @physicsId = id FROM Subjects WHERE name = N'Vật lý';
SELECT @chemistryId = id FROM Subjects WHERE name = N'Hóa học';

-- Thêm topics cho Toán
IF @mathId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Đại số' AND subjectId = @mathId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Đại số', @mathId);
IF @mathId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hình học' AND subjectId = @mathId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hình học', @mathId);

-- Thêm topics cho Vật lý
IF @physicsId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Cơ học' AND subjectId = @physicsId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Cơ học', @physicsId);
IF @physicsId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Điện học' AND subjectId = @physicsId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Điện học', @physicsId);

-- Thêm topics cho Hóa học
IF @chemistryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hóa hữu cơ' AND subjectId = @chemistryId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hóa hữu cơ', @chemistryId);
IF @chemistryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hóa vô cơ' AND subjectId = @chemistryId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hóa vô cơ', @chemistryId);

-- ========================
-- 5. USERS (nếu chưa có)
-- ========================
PRINT '=== ENSURING USERS ===';
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'admin')
    INSERT INTO Users (username, password, email, roleId) VALUES ('admin', 'admin123', 'admin@example.com', 0);
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'teacher')
    INSERT INTO Users (username, password, email, roleId) VALUES ('teacher', 'teacher123', 'teacher@example.com', 1);
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'student')
    INSERT INTO Users (username, password, email, roleId) VALUES ('student', 'student123', 'student@example.com', 2);

-- ========================
-- 6. HIỂN THỊ KẾT QUẢ
-- ========================
PRINT '=== FINAL DATA SUMMARY ===';

PRINT 'ROLES:';
SELECT * FROM Roles ORDER BY id;

PRINT 'DIFFICULTIES:';
SELECT * FROM Difficulties ORDER BY id;

PRINT 'SUBJECTS:';
SELECT * FROM Subjects ORDER BY id;

PRINT 'TOPICS:';
SELECT t.id, t.name, s.name as subject_name 
FROM Topics t 
JOIN Subjects s ON t.subjectId = s.id 
ORDER BY s.id, t.id;

PRINT 'USERS:';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.roleId, u.id;

PRINT 'All data ensured successfully!';
