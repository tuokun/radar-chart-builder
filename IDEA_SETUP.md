# IntelliJ IDEA Project Configuration

This project is configured for IntelliJ IDEA.

## Project Setup

### Prerequisites
- IntelliJ IDEA 2023.2 or later
- JDK 21
- Maven 3.8+

### Importing the Project

1. Open IntelliJ IDEA
2. File → Open → Select `pom.xml` in the project root
3. Choose "Open as Project"
4. Wait for Maven dependencies to download

### Project Structure

```
radar-chart-builder/
├── radar-chart-builder-server/      # Backend module (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── target/                    # Build output (auto-generated)
├── docs/                         # Documentation
├── task_plan.md                  # Development plan
├── findings.md                   # Research findings
└── progress.md                   # Progress log
```

## Important Notes

### Eclipse Files Are Prohibited

⚠️ **This project uses IntelliJ IDEA exclusively.**

- Eclipse project files (`.project`, `.classpath`, `.settings/`, `bin/`) are **FORBIDDEN**
- These files are listed in `.gitignore` and will be ignored
- If you accidentally generate them, run: `mvn clean`

### Build Output

- Maven build output: `target/`
- Compiled classes: `target/classes/`
- Test classes: `target/test-classes/`
- JAR file: `target/radar-chart-builder-server-1.0.0.jar`

### Maven Goals

Common Maven commands:
```bash
# Clean build (also removes any Eclipse files)
mvn clean

# Compile
mvn compile

# Run tests
mvn test

# Package
mvn package

# Run the application
mvn spring-boot:run
```

### Code Style

- Use IntelliJ IDEA's default code style
- Import organization: Organize imports on save
- Format code: Ctrl+Alt+L (Windows/Linux) / Cmd+Opt+L (Mac)

### Running the Application

#### From IDEA:
1. Open `RadarChartApplication.java`
2. Right-click → Run 'RadarChartApplication'

#### From command line:
```bash
cd radar-chart-builder-server
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Database Configuration

Configure MariaDB connection in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/radar_chart_db
    username: root
    password: your-password
```

### Troubleshooting

#### "Cannot resolve symbol" errors
- Invalidate caches: File → Invalidate Caches → Invalidate and Restart

#### Maven dependency issues
- Reload Maven: Right-click on pom.xml → Maven → Reload Project

#### Eclipse files keep appearing
- Run `mvn clean` to remove them
- Check if any Maven plugin is generating them
- Verify `.gitignore` contains Eclipse exclusions

## Development Workflow

1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes
3. Run tests: `mvn test`
4. Commit changes: `git commit -m "feat: your feature"`
5. Push and create pull request

## Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- MyBatis-Plus Documentation: https://baomidou.com/
- IntelliJ IDEA Documentation: https://www.jetbrains.com/idea/
