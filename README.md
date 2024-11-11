# Project
The application is a comprehensive tool designed to simplify the interview process for both jobseekers and interviewers.
It provides features for registering users, managing skills, scheduling interviews, updating questions in real time, and collecting feedback.
In addition, the application offers features for HR to search for specialists with specific skills.
## Contents

- [Functionality](#functionality)
- [Microservices Diagram](#microservices-diagram)
- [ER Diagram](#er-diagram)
- [Roles](#roles)
- [Registration process](#registration)
  - [Usecase diagram]()
  - [Sequence diagram]()
- [Interview process](#interview)
  - [Usecase diagram]()
  - [Block schema]()
- [Skills](#skills)
  - [Skill tree structure](#skill-tree-structure)
  - [Skill match](#skill-match)
- [Question bank](#question-bank)
  - [Question source](#questions-source)
  - [Question bank filling](#question-bank-filling)
- [API Endpoints](#api-endpoints)
- [Authorization Rules](#authorization-rules)

## Functionality

- **User registration:** The user can register on the service by filling out the registration form   and confirming his/her email. After that, the account is activated.

- **Adding skills to the profile:** Users can add their skills to the profile by filling out the appropriate form and saving the changes.

- **Conducting interviews:** The interviewer can conduct an interview using the question and answer page and evaluate the answers. Interview data is saved for later review.

- **View the list of interviews:** The interviewer can view the list of interviews and select a specific one to view details.

- **Saving interview questions:** The interviewer can save the questions they ask during the interview for future reference.

- **Feedback page:** The interviewer can leave feedback about the interview, which is stored in the database.

- **Selecting questions from the bank:** The interviewer can select questions from a pre-saved bank of questions by skill.

- **Real-time question updates:** The candidate can see the interview questions in real time without refreshing the page.

- **Instant feedback:** The candidate receives feedback immediately after the interview is completed.

- **Questions from GPT:** Interviewer can get a list of questions from GPT chat by skill or keyword.

- **Interview reminders:** The user receives reminders about the interview via email and on the page.

## Microservices Diagram
<p align="center">
  <img src="imgs/interview/microservices_architecture.png" alt="Microservices diagram"/>
</p>

[//]: # (![Microservices diagram]&#40;/imgs/microservices_architecture.png&#41;)

## ER Diagram
<p align="center">
  <img src="imgs/db/er_diagram_v2.png" alt="ER diagram"/>
</p>

[//]: # (![ER diagram]&#40;/imgs/er_diagram.png&#41;)

## Roles
![Roles](/imgs/user/roles.png)

## Registration
### Usecase
<p align="center">
  <img src="imgs/user/registration_usecase.png" alt="Registration usecase diagram"/>
</p>

[//]: # (![Registration usecase diagram]&#40;/imgs/registration_usecase.png&#41;)
### Sequence
<p align="center">
  <img src="imgs/user/registration_sequence.png" alt="Registration sequence diagram"/>
</p>

[//]: # (![Registration sequence diagram]&#40;/imgs/registration_sequence.png&#41;)
<p align="center">
  <img src="imgs/interview/interviewer_verification_sequence.png" alt="Interviewer verification diagram"/>
</p>

[//]: # (![Interviewer verification diagram]&#40;/imgs/interviewer_verification_sequence.png&#41;)

## Interview
### Usecase
<p align="center">
  <img src="imgs/interview/interview_usecase.png" alt="Interview usecase diagram"/>
</p>

[//]: # (![Interview usecase diagram]&#40;/imgs/interview_usecase.png&#41;)
### Block schema
<p align="center">
  <img src="imgs/interview/interview_block_schema.png" alt="Interview block schema"/>
</p>

[//]: # (![Interview block schema]&#40;/imgs/interview_block_schema.png&#41;)
## Skills
### Skill tree structure
<p align="center">
  <img src="imgs/skills/skill_tree.png" alt="Skill tree"/>
</p>

[//]: # (![Skill tree]&#40;/imgs/skill_tree.png&#41;)
### Skill match
<p align="center">
  <img src="imgs/skills/skill_match.png" alt="Skill match diagram"/>
</p>

[//]: # (![Skill match diagram]&#40;/imgs/skill_match.png&#41;)

## Question bank
### Questions source
<p align="center">
  <img src="imgs/interview/question_source.png" alt="Question source"/>
</p>

[//]: # (![Question source]&#40;/imgs/question_source.png&#41;)
### Question bank filling
<p align="center">
  <img src="imgs/interview/question_bank_sequence.png" alt="Question bank"/>
</p>

[//]: # (![Question bank]&#40;/imgs/question_bank_sequence.png&#41;)


## API Endpoints

### Authentication Endpoints
- **Register a new user**
  - **POST** `/auth/register`
  - Registers a new user.
  - **Responses:**
    - `200 OK` - Registration successful
    - `400 Bad Request` - Invalid input
- **Authenticate user**
  - **POST** `/auth/login`
  - Authenticates a user and provides authentication tokens.
  - **Responses:**
    - `200 OK` - Authentication successful
    - `401 Unauthorized` - Invalid credentials
    - `404 Not Found` - User not found

### Interview Management Endpoints
- **Get interview results**
  - **GET** `/interviews/{interviewId}/results`
  - Retrieves the results of a specific interview.
  - **Responses:**
    - `200 OK` - Interview results found
    - `404 Not Found` - Interview not found
- **Get interview by ID**
  - **GET** `/interviews/{interviewId}`
  - Retrieves details of a specific interview by its ID.
  - **Responses:**
    - `200 OK` - Interview found
    - `404 Not Found` - Interview not found
- **Get all interviews**
  - **GET** `/interviews`
  - Retrieves all interviews of the authenticated user.
  - **Responses:**
    - `200 OK` - Interviews found
- **Update interview**
  - **PATCH** `/interviews/{interviewId}`
  - Updates an interview before it starts.
  - **Responses:**
    - `200 OK` - Interview updated successfully
    - `404 Not Found` - Interview not found
    - `400 Bad Request` - Invalid interview data
- **Update interview status**
  - **PATCH** `/interviews/status/{interviewId}`
  - Updates the status of a specific interview.
  - **Responses:**
    - `200 OK` - Interview status updated successfully
    - `404 Not Found` - Interview not found
    - `400 Bad Request` - Invalid interview status
- **Create interview**
  - **POST** `/interviews`
  - Creates a new interview.
  - **Responses:**
    - `201 Created` - Interview created successfully
    - `400 Bad Request` - Invalid interview data
- **Save question**
  - **POST** `/interviews/question`
  - Saves a new question for an interview.
  - **Responses:**
    - `200 OK` - Question saved successfully
    - `400 Bad Request` - Invalid question data
- **Evaluate question**
  - **PATCH** `/interviews/question/grade`
  - Evaluates (grades) a specific interview question.
  - **Responses:**
    - `200 OK` - Question evaluated successfully
    - `400 Bad Request` - Invalid question data
- **Get questions by skill ID**
  - **GET** `/interviews/questions/skill/{id}`
  - Retrieves questions related to a specific skill ID.
  - **Responses:**
    - `200 OK` - Questions found
- **Update feedback**
  - **PATCH** `/interviews/{interviewId}/feedback`
  - Updates feedback for a specific interview.
  - **Responses:**
    - `200 OK` - Feedback updated successfully
    - `400 Bad Request` - Invalid input

### Skill Management Endpoints
- **Get skill tree**
  - **GET** `/skills`
  - Retrieves the skill tree.
  - **Responses:**
    - `200 OK` - Skill tree found successfully
    - `401 Unauthorized` - Unauthorized access
- **Add user skills**
  - **POST** `/user/skills`
  - Adds skills for a user based on role ID.
  - **Responses:**
    - `200 OK` - User skills added successfully
    - `401 Unauthorized` - Unauthorized access

### User Management Endpoints
- **Get information about all users**
  - **GET** `/users`
  - Retrieves information about all users with pagination support.
  - **Responses:**
    - `200 OK` - Users retrieved successfully
- **Get information about a user by email**
  - **POST** `/users/by-email`
  - Retrieves information about a user based on their email address.
  - **Responses:**
    - `200 OK` - User found
    - `404 Not Found` - User not found
- **Get information about a user by ID**
  - **GET** `/users/{id}`
  - Retrieves information about a user based on their ID.
  - **Responses:**
    - `200 OK` - User found
    - `404 Not Found` - User not found
- **Change user password**
  - **PATCH** `/users/password`
  - Changes the password of the authenticated user.
  - **Responses:**
    - `200 OK` - Password changed successfully
- **Delete the authenticated user**
  - **DELETE** `/users`
  - Deletes the authenticated user's account.
  - **Responses:**
    - `200 OK` - User deleted successfully

## Authorization Rules
### User
**Permissions:**
  - Register and update personal profile with skills.
  - Participate in mock interviews.
  - View their own interview results and feedback.
  - Receive interview reminders and notifications.
### Admin
**Permissions:**
  - Manage user accounts (create, edit, delete users).
  - View and manage all interviews and questions.
