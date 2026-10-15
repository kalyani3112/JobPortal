CREATE DATABASE placementdb;

USE placementdb;

CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    branch VARCHAR(50),
    cgpa DECIMAL(3,2),
    graduation_year INT
);

INSERT INTO students
(name, email, phone, branch, cgpa, graduation_year)
VALUES
('Kalyani', 'kalyani@gmail.com', '9876543210',
'CSE', 9.58, 2027);

SELECT * FROM students;