-- Script khởi tạo database hoàn chỉnh theo requirement
USE quanlytracnghiem;

-- ========================
-- 1. Xóa dữ liệu cũ nếu có
-- ========================
DELETE FROM UserAnswers;
DELETE FROM ExamResults;
DELETE FROM Exam_Questions;
DELETE FROM Exams;
DELETE FROM Question_CorrectAnswers;
DELETE FROM Questions;
DELETE FROM Topics;
DELETE FROM Subjects;
DELETE FROM Users;
DELETE FROM Difficulties;
DELETE FROM Roles;

-- ========================
-- 2. INSERT ROLES (không có IDENTITY)
-- ========================
INSERT INTO Roles (id, name) VALUES (0, 'admin');
INSERT INTO Roles (id, name) VALUES (1, 'teacher');
INSERT INTO Roles (id, name) VALUES (2, 'student');

-- ========================
-- 3. INSERT DIFFICULTIES (không có IDENTITY)
-- ========================
INSERT INTO Difficulties (id, level) VALUES (0, 'Easy');
INSERT INTO Difficulties (id, level) VALUES (1, 'Medium');
INSERT INTO Difficulties (id, level) VALUES (2, 'Hard');

-- ========================
-- 4. INSERT USERS MẪU
-- ========================
INSERT INTO Users (username, password, email, roleId) 
VALUES ('admin', 'admin123', 'admin@example.com', 0);

INSERT INTO Users (username, password, email, roleId) 
VALUES ('teacher', 'teacher123', 'teacher@example.com', 1);

INSERT INTO Users (username, password, email, roleId) 
VALUES ('student', 'student123', 'student@example.com', 2);

-- ========================
-- 5. INSERT SUBJECTS MẪU
-- ========================
INSERT INTO Subjects (name, description) 
VALUES ('Toán học', 'Môn học về số học, đại số, hình học');

INSERT INTO Subjects (name, description) 
VALUES ('Vật lý', 'Môn học về các hiện tượng tự nhiên');

INSERT INTO Subjects (name, description) 
VALUES ('Hóa học', 'Môn học về cấu tạo và tính chất của chất');

-- ========================
-- 6. INSERT TOPICS MẪU
-- ========================
INSERT INTO Topics (name, subjectId) VALUES ('Đại số', 1);
INSERT INTO Topics (name, subjectId) VALUES ('Hình học', 1);
INSERT INTO Topics (name, subjectId) VALUES ('Cơ học', 2);
INSERT INTO Topics (name, subjectId) VALUES ('Điện học', 2);
INSERT INTO Topics (name, subjectId) VALUES ('Hóa hữu cơ', 3);
INSERT INTO Topics (name, subjectId) VALUES ('Hóa vô cơ', 3);

-- ========================
-- 7. INSERT QUESTIONS MẪU
-- ========================
INSERT INTO Questions (content, optionA, optionB, optionC, optionD, topicId, difficultyId, subjectId, createdBy)
VALUES ('2 + 2 = ?', '3', '4', '5', '6', 1, 0, 1, 1);

INSERT INTO Questions (content, optionA, optionB, optionC, optionD, topicId, difficultyId, subjectId, createdBy)
VALUES ('Công thức tính diện tích hình vuông là?', 'a × b', 'a²', '2a', 'a + b', 2, 0, 1, 1);

INSERT INTO Questions (content, optionA, optionB, optionC, optionD, topicId, difficultyId, subjectId, createdBy)
VALUES ('Định luật Newton thứ nhất còn được gọi là?', 'Định luật quán tính', 'Định luật tác dụng phản tác dụng', 'Định luật hấp dẫn', 'Định luật bảo toàn năng lượng', 3, 1, 2, 1);

-- ========================
-- 8. INSERT CORRECT ANSWERS
-- ========================
INSERT INTO Question_CorrectAnswers (questionId, optionLabel) VALUES (1, 'B');
INSERT INTO Question_CorrectAnswers (questionId, optionLabel) VALUES (2, 'B');
INSERT INTO Question_CorrectAnswers (questionId, optionLabel) VALUES (3, 'A');

-- ========================
-- 9. INSERT EXAMS MẪU
-- ========================
INSERT INTO Exams (title, duration, subjectId, createdBy)
VALUES ('Đề thi Toán cơ bản', 60, 1, 1);

INSERT INTO Exams (title, duration, subjectId, createdBy)
VALUES ('Đề thi Vật lý nâng cao', 90, 2, 1);

-- ========================
-- 10. INSERT EXAM_QUESTIONS
-- ========================
INSERT INTO Exam_Questions (examId, questionId) VALUES (1, 1);
INSERT INTO Exam_Questions (examId, questionId) VALUES (1, 2);
INSERT INTO Exam_Questions (examId, questionId) VALUES (2, 3);

-- ========================
-- 11. HIỂN THỊ KẾT QUẢ
-- ========================
PRINT '=== ROLES ===';
SELECT * FROM Roles ORDER BY id;

PRINT '=== DIFFICULTIES ===';
SELECT * FROM Difficulties ORDER BY id;

PRINT '=== USERS ===';
SELECT u.username, u.email, r.name as role_name 
FROM Users u 
JOIN Roles r ON u.roleId = r.id 
ORDER BY u.roleId;

PRINT '=== SUBJECTS ===';
SELECT * FROM Subjects;

PRINT '=== TOPICS ===';
SELECT t.name, s.name as subject_name 
FROM Topics t 
JOIN Subjects s ON t.subjectId = s.id;

PRINT '=== QUESTIONS ===';
SELECT q.id, q.content, s.name as subject, t.name as topic, d.level as difficulty
FROM Questions q
JOIN Subjects s ON q.subjectId = s.id
JOIN Topics t ON q.topicId = t.id
JOIN Difficulties d ON q.difficultyId = d.id;

PRINT 'Database initialization completed successfully!';
