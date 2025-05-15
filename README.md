# Online Quiz Application

A Java-based desktop application for creating and taking quizzes, with user authentication and progress tracking.

## Features

- User authentication with secure password storage
- Admin panel for quiz management
- Interactive quiz-taking interface
- Progress tracking and score history
- SQLite database integration
- Modern Swing-based UI

## Prerequisites

- Java JDK 11 or higher
- Maven
- SQLite

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Sandesh0527/-Online-Quiz-Application
```

2. Navigate to the project directory:
```bash
cd online-quiz-application
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
java -jar target/online-quiz-application-1.0-SNAPSHOT.jar
```

## Default Admin Account

- Username: admin
- Password: admin123

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── quizapp/
│   │           ├── dao/         # Data Access Objects
│   │           ├── model/       # Data Models
│   │           ├── ui/          # User Interface
│   │           └── util/        # Utilities
│   └── resources/              # Application Resources
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
