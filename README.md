# Online Exam Management System

A comprehensive web-based application for managing online examinations, built with Spring Boot and Java 17.

## 📋 Overview

The Online Exam Management System provides a complete solution for educational institutions to create, manage, and conduct online examinations. The system supports multiple user roles including administrators, teachers, students, and exam reviewers with specific permissions and capabilities tailored to each role.

## 🚀 Features

### User Management
- **Multi-role system**: Admin, Teacher, Student, Exam Reviewer
- **Authentication**: JWT-based secure authentication
- **Profile Management**: Users can update personal information and credentials

### Course Management
- Course creation and organization by department
- Student enrollment management
- Teacher assignment to courses

### Exam Management
- Comprehensive exam creation with various question types
- Time-bound exams with customizable duration
- Question banks for reusing questions
- Automated and manual grading options
- Exam review process with feedback

### Student Features
- Easy access to enrolled courses
- Scheduled exam calendar
- Real-time exam taking with time tracking
- Immediate results for objective questions
- Progress tracking across courses

### Teacher Features
- Course and exam creation
- Student performance analytics
- Question management and organization
- Detailed reporting on exam results

### Exam Reviewer Features
- Quality control of exam content
- Feedback on exam structure and questions
- Approval workflow for exam publishing

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.3
- **Security**: Spring Security with JWT
- **Database**: MySQL 8
- **ORM**: Hibernate/JPA
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Build Tool**: Maven

## 📊 Database Schema

The system uses a relational database with the following key entities:
- Users (with extensions for Teacher, Student, Exam Reviewer)
- Courses
- Exams
- Questions & Options
- Submissions & Answers
- Departments & Faculties
- Exam Reviews

## 🔧 Setup and Installation

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- MySQL 8.0
- Maven 3.6+
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/GezahegnTsegaye/online-exam-system.git
   cd online-exam-system
   ```

2. **Configure MySQL Database**

   Ensure MySQL is running and create a user for the application:
   ```sql
   CREATE USER 'online'@'localhost' IDENTIFIED BY 'admin';
   GRANT ALL PRIVILEGES ON *.* TO 'online'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Application**

   The `application.yml` file contains the configuration for the application. Modify as needed:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/exam?createDatabaseIfNotExist=true
       username: online
       password: admin
   ```

4. **Build the Application**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the Application**

   The application will be available at: [http://localhost:8080/api](http://localhost:8080/api)

   Swagger documentation: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)

## 🔒 Security Configuration

The application uses JWT (JSON Web Token) for authentication. Token expiration and secret key can be configured in the application.yml file:

```yaml
security:
  jwt:
    secret-key: your-secret-key
    expiration: 86400000  # 24 hours in milliseconds
```

## 📚 API Documentation

The API is documented using OpenAPI 3.0 and can be accessed through Swagger UI at `/api/swagger-ui.html`. This provides a complete overview of all available endpoints, required parameters, and response formats.

## 👥 User Roles and Permissions

### Administrator
- System-wide access
- User management
- Department and faculty management

### Teacher
- Course creation and management
- Exam creation and grading
- Student performance tracking

### Student
- Course enrollment
- Exam participation
- Performance review

### Exam Reviewer
- Exam content review
- Quality assurance
- Feedback provision

## 🧪 Testing

Run the tests using Maven:
```bash
mvn test
```

The application includes:
- Unit tests for services and controllers
- Integration tests for repositories
- End-to-end API tests

## 🚢 Deployment

### Prerequisites for Production Deployment
- Java 17 runtime
- MySQL 8 database server
- Reverse proxy (e.g., Nginx) for SSL termination

### Production Configuration
For production deployment, update the following configurations:
- Use environment variables for sensitive information
- Configure HTTPS
- Set appropriate logging levels
- Implement database backup strategy

## 📝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push the branch: `git push origin feature/new-feature`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Authors and Contributors

- **Gezahegn** - *Initial work* - [YourGitHub](https://github.com/GezahegnTsegaye)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- The educational institutions that provided requirements and feedback