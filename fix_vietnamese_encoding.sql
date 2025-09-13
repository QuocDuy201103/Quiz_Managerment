-- Script sửa vấn đề encoding tiếng Việt
USE quanlytracnghiem;

-- ========================
-- 1. Kiểm tra collation hiện tại
-- ========================
PRINT '=== COLLATION HIỆN TẠI ===';
SELECT 
    name,
    collation_name
FROM sys.databases 
WHERE name = 'quanlytracnghiem';

-- ========================
-- 2. Kiểm tra dữ liệu tiếng Việt hiện có
-- ========================
PRINT '=== DỮ LIỆU TIẾNG VIỆT HIỆN CÓ ===';
SELECT 
    id,
    name,
    description
FROM Subjects
WHERE name LIKE N'%Toán%' OR name LIKE N'%Vật%' OR name LIKE N'%Hóa%';

-- ========================
-- 3. Cập nhật dữ liệu tiếng Việt (nếu bị lỗi)
-- ========================
PRINT '=== CẬP NHẬT DỮ LIỆU TIẾNG VIỆT ===';

-- Cập nhật subjects
UPDATE Subjects 
SET name = N'Toán học', description = N'Môn học về số học, đại số, hình học'
WHERE id = 1;

UPDATE Subjects 
SET name = N'Vật lý', description = N'Môn học về các hiện tượng tự nhiên'
WHERE id = 2;

UPDATE Subjects 
SET name = N'Hóa học', description = N'Môn học về cấu tạo và tính chất của chất'
WHERE id = 3;

-- Cập nhật topics
UPDATE Topics 
SET name = N'Đại số'
WHERE id = 1;

UPDATE Topics 
SET name = N'Hình học'
WHERE id = 2;

UPDATE Topics 
SET name = N'Cơ học'
WHERE id = 3;

UPDATE Topics 
SET name = N'Điện học'
WHERE id = 4;

UPDATE Topics 
SET name = N'Hóa hữu cơ'
WHERE id = 5;

UPDATE Topics 
SET name = N'Hóa vô cơ'
WHERE id = 6;

-- Cập nhật difficulties
UPDATE Difficulties 
SET level = N'Easy'
WHERE id = 0;

UPDATE Difficulties 
SET level = N'Medium'
WHERE id = 1;

UPDATE Difficulties 
SET level = N'Hard'
WHERE id = 2;

-- Cập nhật questions
UPDATE Questions 
SET content = N'2 + 2 = ?'
WHERE id = 1;

UPDATE Questions 
SET content = N'Công thức tính diện tích hình vuông là?'
WHERE id = 2;

UPDATE Questions 
SET content = N'Định luật Newton thứ nhất còn được gọi là?'
WHERE id = 3;

-- ========================
-- 4. Kiểm tra kết quả
-- ========================
PRINT '=== KẾT QUẢ SAU KHI CẬP NHẬT ===';
SELECT 
    id,
    name,
    description
FROM Subjects
ORDER BY id;

SELECT 
    id,
    name,
    level
FROM Difficulties
ORDER BY id;

SELECT 
    id,
    content
FROM Questions
ORDER BY id;

PRINT 'Vietnamese encoding fix completed!';
