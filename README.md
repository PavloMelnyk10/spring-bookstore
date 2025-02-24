# Online Bookstore

## Project Description

This project is a web application for managing an online bookstore. It provides functionality for browsing books, managing categories, placing orders, and user authentication. The backend is powered by **Spring Boot** and follows RESTful principles to ensure scalable and maintainable architecture

The main purpose of this project is to showcase my skills in backend development, including the design and implementation of modern web applications using **Spring Boot** and related technologies

The Online Bookstore is a platform where users can:
- Browse, search, and view books
- Add books to a shopping cart and place orders
- Manage their orders and view order history

Administrators can:
- Manage the inventory (books) and categories
- Update the status of orders

JWT-based authentication is implemented for secure user management

## Live Demo

The project has been deployed and is accessible for exploration at:

**[Swagger UI - Online Bookstore](http://174.129.123.243/api/swagger-ui/index.html)**

### Important Note:
All users registered through the demo API are automatically assigned the `USER` role. This means they have access to **non-administrative functionalities only**, such as:
- Browsing books and categories
- Adding books to their shopping cart
- Placing and managing their orders

For full administrative capabilities, users need the `ADMIN` role, which is not assignable through the demo

---


## Technologies

### Programming Language
- **Java 21** — Programming language

### Frameworks
- **Spring Boot 3.3.5** — Web application framework for rapid development
- **Spring Security** — Authentication and authorization
- **Spring Data JPA** — Simplifies database interactions

### Database and Migrations
- **MySQL** — Database used in production
- **Liquibase** — Tool for managing database schema migrations

### Authentication and Security
- **JWT** — JSON Web Tokens for secure user authentication

### API Documentation
- **Swagger** — Tool for generating API documentation

### Development Tools
- **MapStruct** — Simplifies mapping between DTOs and entities
- **Maven** — Dependency management and build tool

### Testing
- **Testcontainers** — Provides real containerized environments for testing
- **JUnit 5** — Testing framework for unit and integration tests
- **Mockito** — Mocking framework for unit tests

### Containerization
- **Docker** — For consistent runtime environments
- **Docker Compose** — Orchestrates multi-container applications

## API Endpoints

### Authentication

- **POST /api/auth/register** — Register a new user
- **POST /api/auth/login** — User login to obtain JWT

### Book Endpoints

- **GET /api/books** — Get a list of all books (User role required)
- **GET /api/books/{id}** — Get information about a specific book (User/Admin role)
- **POST /api/books** — Add a new book (Admin role required)
- **PUT /api/books/{id}** — Update a book (Admin role required)
- **DELETE /api/books/{id}** — Delete a book (Admin role required)
- **GET /api/books/search** — Search for books using parameters (User role required)

### Category Endpoints

- **GET /api/categories** — Get a list of all categories (User role required)
- **GET /api/categories/{id}** — Get a specific category by ID (User role required)
- **POST /api/categories** — Add a new category (Admin role required)
- **PATCH /api/categories/{id}** — Update a category (Admin role required)
- **DELETE /api/categories/{id}** — Delete a category (Admin role required)
- **GET /api/categories/{id}/books** — Get books by category (User role required)

### Shopping Cart Endpoints

- **GET /api/cart** — Get the user's shopping cart (User role required)
- **POST /api/cart** — Add a book to the shopping cart (User role required)
- **PUT /api/cart/items/{cartItemId}** — Update book quantity in the shopping cart (User role required)
- **DELETE /api/cart/items/{cartItemId}** — Remove a book from the shopping cart (User role required)

### Order Endpoints

- **POST /api/orders** — Place an order (User role required)
- **GET /api/orders** — Get the user's order history (User role required)
- **GET /api/orders/{orderId}/items** — Get the items of a specific order (User role required)
- **GET /api/orders/{orderId}/items/{itemId}** — Get a specific order item (User role required)
- **PATCH /api/orders/{id}** — Update the status of an order (Admin role required)

## Installation and Setup

### Prerequisites
Make sure you have the following technologies installed:
- Java 21
- Maven
- Docker
- Docker Compose
- MySQL

### Cloning the Project
```bash
git clone https://github.com/yourusername/online-bookstore.git
cd online-bookstore
```

### Setting Up the Database
1. Create a database named `bookstore` in your MySQL instance.
2. Update the `application.properties` and `liquibase.properties` files with your MySQL credentials.

### Running the Project
1. Build the project using Maven:
   ```bash
   mvn clean install
   ```
2. Start the application:
   ```bash
   mvn spring-boot:run
   ```

### Running with Docker
To run the application using Docker:
1. Copy the sample environment file and update the values as needed:
   ```bash
   cp .env.sample .env
   ```
2. Build the Docker images and start the containers:
   ```bash
   docker-compose up --build
   ```

### Accessing the Application
- The application will be accessible at `http://localhost:8080/api`.
- Swagger UI will be available at `http://localhost:8080/api/swagger-ui.html`.

