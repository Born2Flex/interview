# Interview

Interview management System is designed to simplify the interview process, making it more efficient and convenient for both interviewers and candidates. 

Users can select and manage their skills with special skill-tree structure. Interview process is conducted based on the skills that intersects between the interviewer and the candidate. 
During interview interviewers can select questions from a pool of saved ones, according to the skill, or generate new questions using the OpenAI API. Also, interviewer can quickly grade candidates' responses and provide immediate feedback in form of a grade from 0 to 100. 
After the interview, interviewers provide detailed feedback to candidates where they write their impression and advices for improvement, candidates can then view their results and gain insights about their performance. 

Future enhancements include implementing candidate search functionality based on skills and developing a graphical user interface to improve user experience.

## Documentation

The documentation is divided into several sections:

- [Functionality](docs/functionality.md)
- [API Description](docs/api_description.md)
- [Roles](docs/roles.md)
- [Business Cases](docs/business_cases.md)
- [Architecture diagram](docs/architecture_diagram.md)
- [ER Diagram](docs/er_diagram.md)

## Run locally

### Database configuration

Interview Service uses MongoDB. You can configure the database connection by specifying your `spring.data.mongo.uri` in `application.yaml`. 
Or, if you have Docker installed, you can use the provided `docker-compose.yml` file to start the MongoDB container.

To start the MongoDB container using Docker, run the following command:
```bash
docker compose up
```

### Build & Start

Execute script to build and start Interview Service locally:
```bash
git clone https://github.com/Born2Flex/interview.git
cd interview
./mvnw package
java -jar target/*.jar
```
Once the application started, you can access the Interview Service API at <http://localhost:8080/swagger-ui/index.html>.

### File links
|Spring Boot Configuration | Class or Java property files                                                                                                                                        |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|The Main Class | [InterviewApplication](src/main/java/ua/edu/internship/interview/InterviewApplication.java) |
|Properties Files | [application.yaml](src/main/resources/application.yaml)                                                                                                          |
