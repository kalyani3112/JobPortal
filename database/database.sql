
CREATE DATABASE IF NOT EXISTS placementdb;

USE placementdb;

-- Students table
CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    branch VARCHAR(50),
    cgpa DECIMAL(3,2),
    graduation_year INT
);

-- Companies table
CREATE TABLE IF NOT EXISTS companies (
    company_id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100) NOT NULL
);

-- Jobs table
CREATE TABLE IF NOT EXISTS jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    job_role VARCHAR(100) NOT NULL,
    company_id INT,
    location VARCHAR(100),
    skills VARCHAR(255),
    FOREIGN KEY (company_id)
        REFERENCES companies(company_id)
);

-- Applications table
CREATE TABLE IF NOT EXISTS applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    job_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'Applied',
    applied_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (student_id)
        REFERENCES students(student_id),
    FOREIGN KEY (job_id)
        REFERENCES jobs(job_id)
);