# Microservices Diagram

<p align="center">
  <img src="./imgs/interview/microservices_architecture.png" alt="Microservices diagram"/>
</p>

**Browser:** 

- Acts as the front-end interface where users (job seekers, HR professionals, and interviewers) interact with
the platform.

**API Gateway:** 

- The entry point for all client requests. It routes requests to appropriate microservices (e.g., /users for user-related operations and /interviews for interview-related operations).

**User Service:**

- Handles user management and authentication.
- Stores user profiles and credentials in a PostgreSQL database.
- Provides tools for candidates to create detailed profiles.

**Interview Service:**

- Manages the core business logic related to interviews.
- Stores interview-related data in MongoDB.
- Provides tools like question banks and integrates with LLMs to generate customized interview questions.

**Notification Service:**

   - Sends notifications to users via email or other communication channels.
   - Uses a message broker (RabbitMQ) for asynchronous processing of notification tasks.

**Message Broker (RabbitMQ):**

- Facilitates communication between microservices (e.g., sending notifications when a new interview is scheduled).
- Decouples services, ensuring scalability and fault tolerance.

**Databases:**

- PostgreSQL for relational data storage related to users.
- MongoDB for storing non-relational data, such as interview records.
