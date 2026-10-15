# Job Application Portal

A web-based Job Application Portal developed using Java, MySQL, HTML, CSS, and JavaScript.

## Features
- User registration and login
- Secure password hashing for newly registered users using PBKDF2 with HMAC-SHA256
- View available jobs
- Search jobs
- Apply for jobs
- View submitted applications
- Store job and application details in MySQL

## Technologies Used
- Frontend: HTML, CSS, JavaScript
- Backend: Java
- Database: MySQL
- Database Connectivity: JDBC
- Tools: Visual Studio Code, MySQL Workbench, Git, GitHub

## Project Structure
- `backend/` - Java backend source code and MySQL JDBC driver
- `database/` - SQL database setup
- `frontend/` - HTML, CSS, and JavaScript files

## Prerequisites
- Java JDK
- MySQL Server
- Visual Studio Code
- Live Server extension

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/kalyani3112/JobPortal.git
cd JobPortal
```

### 2. Set up the database
Open MySQL Workbench and execute `database/database.sql`.

### 3. Configure the database password
In PowerShell, from the project root, run:
```powershell
$env:DB_PASSWORD = Read-Host "Enter MySQL password"
```

### 4. Compile the backend
```powershell
javac -cp "backend/lib/mysql-connector-j-26.7.0.jar" -d backend/out (Get-ChildItem -Recurse backend/src/main/java -Filter *.java | ForEach-Object { $_.FullName })
```

### 5. Run the backend
```powershell
java -cp "backend/out;backend/lib/mysql-connector-j-26.7.0.jar" com.jobportal.JobServer
```

The backend runs at `http://localhost:8080`.

### 6. Run the frontend
Open `frontend/index.html` in VS Code and select **Open with Live Server**.

## Testing
The project has been tested locally for Java compilation, database connectivity, registration, login, job listing, job application, and application retrieval.

## Future Enhancements
- Password reset and recovery functionality
- Recruiter accounts and role-based access
- Improved validation and error handling
- Online deployment

## Author
Kalyani

GitHub: https://github.com/kalyani3112
