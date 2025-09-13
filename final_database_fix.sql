-- Script tổng hợp sửa tất cả lỗi FOREIGN KEY constraint và đảm bảo dữ liệu đầy đủ
USE quanlytracnghiem;

PRINT '=== FINAL DATABASE FIX - COMPREHENSIVE SOLUTION ===';

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
-- 3. USERS (Comprehensive)
-- ========================
PRINT '=== ENSURING USERS ===';
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

-- Student users (ID = 3, 4, 5, 6, 7)
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 3)
BEGIN
    PRINT 'Adding student user with ID = 3...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (3, 'student', 'student123', 'student@example.com', 2);
END

IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 4)
BEGIN
    PRINT 'Adding student user with ID = 4...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (4, 'hocsinh1', 'hocsinh123', 'hocsinh1@example.com', 2);
END

IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 5)
BEGIN
    PRINT 'Adding student user with ID = 5...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (5, 'hocsinh2', 'hocsinh123', 'hocsinh2@example.com', 2);
END

IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 6)
BEGIN
    PRINT 'Adding student user with ID = 6...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (6, 'hocsinh3', 'hocsinh123', 'hocsinh3@example.com', 2);
END

IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 7)
BEGIN
    PRINT 'Adding student user with ID = 7...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (7, 'hocsinh4', 'hocsinh123', 'hocsinh4@example.com', 2);
END

-- ========================
-- 4. SUBJECTS (nếu chưa có)
-- ========================
PRINT '=== ENSURING SUBJECTS ===';
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Toán học')
    INSERT INTO Subjects (name, description) VALUES (N'Toán học', N'Môn học về số học, đại số, hình học');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Vật lý')
    INSERT INTO Subjects (name, description) VALUES (N'Vật lý', N'Môn học về các hiện tượng tự nhiên');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Hóa học')
    INSERT INTO Subjects (name, description) VALUES (N'Hóa học', N'Môn học về cấu tạo và tính chất của chất');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Tiếng Anh')
    INSERT INTO Subjects (name, description) VALUES (N'Tiếng Anh', N'Môn học về ngôn ngữ tiếng Anh');
IF NOT EXISTS (SELECT 1 FROM Subjects WHERE name = N'Lịch sử')
    INSERT INTO Subjects (name, description) VALUES (N'Lịch sử', N'Môn học về lịch sử Việt Nam và thế giới');

-- ========================
-- 5. TOPICS (nếu chưa có)
-- ========================
PRINT '=== ENSURING TOPICS ===';
-- Lấy subject IDs
DECLARE @mathId INT, @physicsId INT, @chemistryId INT, @englishId INT, @historyId INT;
SELECT @mathId = id FROM Subjects WHERE name = N'Toán học';
SELECT @physicsId = id FROM Subjects WHERE name = N'Vật lý';
SELECT @chemistryId = id FROM Subjects WHERE name = N'Hóa học';
SELECT @englishId = id FROM Subjects WHERE name = N'Tiếng Anh';
SELECT @historyId = id FROM Subjects WHERE name = N'Lịch sử';

-- Thêm topics cho Toán
IF @mathId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Đại số' AND subjectId = @mathId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Đại số', @mathId);
IF @mathId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hình học' AND subjectId = @mathId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hình học', @mathId);
IF @mathId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Giải tích' AND subjectId = @mathId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Giải tích', @mathId);

-- Thêm topics cho Vật lý
IF @physicsId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Cơ học' AND subjectId = @physicsId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Cơ học', @physicsId);
IF @physicsId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Điện học' AND subjectId = @physicsId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Điện học', @physicsId);
IF @physicsId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Quang học' AND subjectId = @physicsId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Quang học', @physicsId);

-- Thêm topics cho Hóa học
IF @chemistryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hóa hữu cơ' AND subjectId = @chemistryId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hóa hữu cơ', @chemistryId);
IF @chemistryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hóa vô cơ' AND subjectId = @chemistryId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hóa vô cơ', @chemistryId);
IF @chemistryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Hóa phân tích' AND subjectId = @chemistryId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Hóa phân tích', @chemistryId);

-- Thêm topics cho Tiếng Anh
IF @englishId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Ngữ pháp' AND subjectId = @englishId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Ngữ pháp', @englishId);
IF @englishId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Từ vựng' AND subjectId = @englishId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Từ vựng', @englishId);
IF @englishId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Đọc hiểu' AND subjectId = @englishId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Đọc hiểu', @englishId);

-- Thêm topics cho Lịch sử
IF @historyId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Lịch sử Việt Nam' AND subjectId = @historyId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Lịch sử Việt Nam', @historyId);
IF @historyId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Topics WHERE name = N'Lịch sử thế giới' AND subjectId = @historyId)
    INSERT INTO Topics (name, subjectId) VALUES (N'Lịch sử thế giới', @historyId);

-- ========================
-- 6. FIX EXISTING DATA
-- ========================
PRINT '=== FIXING EXISTING DATA ===';

-- Sửa Questions có createdBy không hợp lệ
PRINT 'Fixing Questions with invalid createdBy...';
UPDATE Questions 
SET createdBy = 2 
WHERE createdBy NOT IN (SELECT id FROM Users);

-- Sửa Exams có createdBy không hợp lệ
PRINT 'Fixing Exams with invalid createdBy...';
UPDATE Exams 
SET createdBy = 2 
WHERE createdBy NOT IN (SELECT id FROM Users);

-- Sửa ExamResults có userId không hợp lệ
PRINT 'Fixing ExamResults with invalid userId...';
UPDATE ExamResults 
SET userId = 3 
WHERE userId NOT IN (SELECT id FROM Users);

-- ========================
-- 7. FINAL SUMMARY
-- ========================
PRINT '=== FINAL DATA SUMMARY ===';

PRINT 'ROLES:';
SELECT * FROM Roles ORDER BY id;

PRINT 'DIFFICULTIES:';
SELECT * FROM Difficulties ORDER BY id;

PRINT 'USERS:';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;

PRINT 'SUBJECTS:';
SELECT * FROM Subjects ORDER BY id;

PRINT 'TOPICS:';
SELECT t.id, t.name, s.name as subject_name 
FROM Topics t 
JOIN Subjects s ON t.subjectId = s.id 
ORDER BY s.id, t.id;

PRINT 'QUESTIONS:';
SELECT q.id, LEFT(q.content, 50) as content_preview, q.createdBy, u.username as created_by_username
FROM Questions q 
LEFT JOIN Users u ON q.createdBy = u.id 
ORDER BY q.id;

PRINT 'EXAMS:';
SELECT e.id, e.title, e.createdBy, u.username as created_by_username
FROM Exams e 
LEFT JOIN Users u ON e.createdBy = u.id 
ORDER BY e.id;

PRINT 'EXAMRESULTS:';
SELECT er.id, er.userId, u.username, er.examId, er.score, er.submittedAt
FROM ExamResults er 
LEFT JOIN Users u ON er.userId = u.id 
ORDER BY er.id;

PRINT '=== ALL FIXES COMPLETED SUCCESSFULLY! ===';
PRINT 'The database is now ready for the Quiz Management System.';
PRINT 'All foreign key constraints should work properly.';
