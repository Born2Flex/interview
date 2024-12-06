# API Endpoints

## Authentication Endpoints

- **Register a new user**
    - **POST** `/auth/register`
    - Registers a new user.
    - **Responses:**
        - `201 Created` - Registration successful
        - `400 Bad Request` - Invalid input
- **Authenticate user**
    - **POST** `/auth/login`
    - Authenticates a user and provides authentication tokens.
    - **Responses:**
        - `200 OK` - Authentication successful
        - `401 Unauthorized` - Invalid credentials

## User Management Endpoints

- **Register a new user**
    - **POST** `/users`
    - Registers a new user.
    - **Responses:**
        - `201 Created` - User registered successfully
        - `400 Bad Request` - Invalid input
- **Update user data**
    - **PUT** `/users/{id}`
    - Updates the data of a specific user.
    - **Responses:**
        - `200 OK` - Data updated successfully
        - `400 Bad Request` - Invalid input
- **Change user password**
    - **PATCH** `/users/{id}`
    - Changes the password of a specific user.
    - **Responses:**
        - `200 OK` - Password changed successfully
        - `400 Bad Request` - Invalid input
- **Get information about all users**
    - **GET** `/users`
    - Retrieves information about all users.
    - **Responses:**
        - `200 OK` - Users retrieved successfully
- **Get information about a user by email**
    - **GET** `/users/by-email`
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
- **Delete a user**
    - **DELETE** `/users/{id}`
    - Deletes a specific user account.
    - **Responses:**
        - `204 No Content` - User deleted successfully
        - `404 Not Found` - User not found

## Skills Management Endpoints

- **Get all skills**
    - **GET** `/skills`
    - Retrieves a list of all available skill trees.
    - **Responses:**
        - `200 OK` - Skills retrieved successfully
- **Get skill by root ID**
    - **GET** `/skills/{id}`
    - Retrieves a specific skill tree by root ID.
    - **Responses:**
        - `200 OK` - Skill found
        - `404 Not Found` - Skill not found

## User Questions Endpoints

- **Get user questions**
    - **GET** `/users/{userId}/questions`
    - Retrieves a list of all questions for a specific user.
    - **Responses:**
        - `200 OK` - User questions retrieved successfully
        - `404 Not Found` - User not found
- **Create a new user question**
    - **POST** `/users/{userId}/questions`
    - Creates a new question for a specific user.
    - **Responses:**
        - `201 Created` - Question created successfully
        - `400 Bad Request` - Invalid input
- **Update an existing user question**
    - **PUT** `/users/{userId}/questions/{questionId}`
    - Updates an existing user question.
    - **Responses:**
        - `200 OK` - Question updated successfully
        - `400 Bad Request` - Invalid input
        - `404 Not Found` - User or question not found
- **Delete a user question**
    - **DELETE** `/users/{userId}/questions/{questionId}`
    - Deletes a specific question for a user.
    - **Responses:**
        - `204 No Content` - Question deleted successfully
        - `404 Not Found` - User or question not found
- **Get user questions by skill**
    - **GET** `/users/{userId}/questions/skill/{skillId}`
    - Retrieves questions related to a specific skill for a user.
    - **Responses:**
        - `200 OK` - User questions by skill retrieved successfully
        - `404 Not Found` - User or skill not found

## User Skills Endpoints

- **Get skills by user ID**
    - **GET** `/users/{userId}/skills`
    - Retrieves a list of skills associated with a specific user.
    - **Responses:**
        - `200 OK` - User skills retrieved successfully
        - `404 Not Found` - User not found
- **Create user skills**
    - **POST** `/users/{userId}/skills`
    - Creates a set of skills for a user based on skill IDs.
    - **Responses:**
        - `201 Created` - User skills created successfully
        - `400 Bad Request` - Invalid input
- **Update user skills**
    - **PUT** `/users/{userId}/skills`
    - Updates the skills for a user based on skill IDs.
    - **Responses:**
        - `200 OK` - User skills updated successfully
        - `400 Bad Request` - Invalid input

## Interview Questions Endpoints

- **Save question**
    - **POST** `/interviews/{interviewId}/question`
    - Saves a new question for an interview.
    - **Responses:**
        - `200 OK` - Question saved successfully
        - `400 Bad Request` - Invalid question data

- **Evaluate question**
    - **PATCH** `/interviews/{interviewId}/question/{questionId}/grade`
    - Evaluates (grades) a specific interview question.
    - **Responses:**
        - `200 OK` - Question evaluated successfully
        - `400 Bad Request` - Invalid question data

## Interview Management Endpoints

- **Create interview**
    - **POST** `/interviews`
    - Creates a new interview.
    - **Responses:**
        - `201 Created` - Interview created successfully
        - `400 Bad Request` - Invalid interview data
- **Update interview**
    - **PATCH** `/interviews/{interviewId}`
    - Updates an interview before it starts.
    - **Responses:**
        - `200 OK` - Interview updated successfully
        - `400 Bad Request` - Invalid interview data
- **Update interview status**
    - **PATCH** `/interviews/{interviewId}/status`
    - Updates the status of a specific interview.
    - **Responses:**
        - `200 OK` - Interview status updated successfully
        - `404 Not Found` - Interview not found
        - `400 Bad Request` - Invalid interview status
- **Update feedback**
    - **PATCH** `/interviews/{interviewId}/feedback`
    - Updates feedback for a specific interview.
    - **Responses:**
        - `200 OK` - Feedback updated successfully
        - `400 Bad Request` - Invalid input
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
