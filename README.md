# Online Quiz Application

A comprehensive Java-based desktop application for creating and taking quizzes, featuring user authentication, progress tracking, and an intuitive admin interface.

## Features

### User Features
- User registration and authentication
- Take quizzes with multiple-choice questions
- View quiz history and scores
- Track personal progress
- Update profile and password

### Admin Features
- Create and manage quizzes
- Add/edit/delete questions and options
- View all quiz results
- Manage user accounts

### Technical Features
- Secure password hashing with BCrypt
- SQLite database integration
- Modern Swing UI with custom theming
- Transaction management
- Comprehensive error handling

## Prerequisites

- Java JDK 11 or higher
- Maven 3.6.0 or higher
- SQLite 3.36.0 or higher

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Sandesh0527/-Online-Quiz-Application
cd online-quiz-application
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
java -jar target/online-quiz-application-1.0-SNAPSHOT.jar
```

## Default Admin Account
- Username: admin
- Password: admin123

## Project Structure

```
quizapp/
├── dao/                    # Data Access Objects
│   ├── DatabaseInitializer.java
│   ├── QuizDAO.java
│   ├── QuizResultDAO.java
│   └── UserDAO.java
├── model/                  # Data Models
│   ├── Question.java
│   ├── Quiz.java
│   ├── QuizResult.java
│   └── User.java
├── ui/                     # User Interface Components
│   ├── admin/             # Admin Interface
│   ├── quiz/              # Quiz Interface
│   ├── user/              # User Profile Interface
│   ├── LoginFrame.java
│   ├── MainFrame.java
│   ├── RegisterFrame.java
│   └── ResultsPanel.java
├── util/                   # Utilities
│   ├── DatabaseUtil.java
│   ├── PasswordUtil.java
│   └── ThemeManager.java
└── Main.java              # Application Entry Point
```

## Database Schema

The application uses SQLite with the following main tables:
- users
- quizzes
- questions
- options
- quiz_results
- question_results
- selected_options
