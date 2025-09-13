-- Kiểm tra users trong database
USE quanlytracnghiem;

PRINT '=== CHECKING USERS ===';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;

PRINT '=== CHECKING ROLES ===';
SELECT * FROM Roles ORDER BY id;

-- Thêm user admin nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 1)
BEGIN
    PRINT 'Adding admin user with ID = 1...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (1, 'admin', 'admin123', 'admin@example.com', 0);
END

-- Thêm user teacher nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 2)
BEGIN
    PRINT 'Adding teacher user with ID = 2...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (2, 'teacher', 'teacher123', 'teacher@example.com', 1);
END

-- Thêm user student nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Users WHERE id = 3)
BEGIN
    PRINT 'Adding student user with ID = 3...';
    INSERT INTO Users (id, username, password, email, roleId) 
    VALUES (3, 'student', 'student123', 'student@example.com', 2);
END

PRINT '=== FINAL USERS ===';
SELECT u.id, u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.id;
