# Installation Guide - Makibeans Web API

---

## Table of Contents

1. Introduction
2. Requirements
3. Installation Instructions
4. Test Users and Roles
5. Postman Collection
6. REST Endpoints and JSON Examples
7. Swagger API Documentation
8. Additional Commands

---

## 1. Introduction

The Makibeans Web API is a Java Spring Boot backend for a coffee beans webshop that supports management of products and
categories. In addition, products can be dynamically enriched with attributes and variants. The application is secured
with JWT authentication and uses a relational PostgreSQL database. The API supports multiple user roles (admin and
customer) and provides features such as user management, product management, filtering, search, and image upload.

**Technologies used:**

- Java 21
- Spring Boot 3.4.3
- PostgreSQL
- Maven 3.9+
- JWT (io.jsonwebtoken v0.11.5)
- MapStruct (v1.6.3)
- JPA & Hibernate
- Spring Security
- dotenv (spring-dotenv v4.0.0)
- Swagger (springdoc-openapi v2.7.0)
- Apache Tika (v3.1.0)
- Lombok (v1.18.30)

---

## 2. Requirements

To run this web API, you'll need:

- Java JDK 21
- Relational database: PostgreSQL (currently configured on port 5433, but you can change it in the `.env` file and `application.properties`)
- Postman (for API testing)
- A `.env` file with secrets including database username and password and SSL (see step 3)
- Maven 3.9+ (or the Maven wrapper `mvnw` included in the project)

> All secrets in the `.env` file are automatically loaded into `application.properties` using the Spring dotenv
> integration. You can reference them using `${}` placeholders such as `${db.username}`.

---

## 3. Installation Instructions

### Step 1: Create Database

1. Open a terminal on your machine.
2. Start PostgreSQL (e.g., via Postgres.app or pgAdmin).
3. Connect to an existing database <database_name> (e.g., `students`) using the following command:

```bash
psql -p 5433 -d <database_name>
```

4. Create a new database user the application will use to connect:

```sql
CREATE USER <db_username> WITH PASSWORD '<db_password>';
```

5. Create a new database and assign the user as the owner:

```sql
CREATE DATABASE makibeans OWNER <db_username>;
```

6. Grant all privileges to the user:

```sql
GRANT ALL PRIVILEGES ON DATABASE makibeans TO <db_username>;
```

7. Exit the session:

```sql
\q
```

> Replace `<db_username>` and `<db_password>` with the values from your `.env` file. This user is only used for
> connecting the application to the database. Application users (like admin and regular_user) are created automatically
> when the app starts.


### Step 3: Generate SSL Certificate (only required once)

In Spring Boot, HTTPS can be enabled using an SSL certificate. For development purposes, we use a self-signed
certificate that we generate ourselves. You can also disable SSL in `application.properties` for testing.

Open a terminal and enter the following command:

```bash
keytool -genkey -keyalg RSA -alias certificate -keystore certificate.jks -storepass p4ssw0rd -validity 365 -keysize 4096 -storetype pkcs12
```

After answering a few informational prompts, this will generate a certificate file called `certificate.jks`.  
Place this file in your project directory — for example, inside the `resources` folder.

In your `application.properties` file, add the following configuration:

```properties
server.ssl.key-store=classpath:certificate.jks
server.ssl.key-store-type=pkcs12
server.ssl.key-store-password=${keystorepass}
server.ssl.key-password=${keypass}
server.ssl.key-alias=certificate
server.port=8443
```
> Note: The password for the keystore and key are kept in the `.env` file.
---

### Step 3: Create `.env` file

In the root of your project, create a `.env` file with the following content:

```env
db_username=<db_username>
db_password=<db_password>
jwt_secret=<your_jwt_secret>
server_ssl_key_store_password=keystorepass
server_ssl_key_password=keypass
```

> Note : for the JWT key, A good practice is to use a randomly generated string of at least 32 characters. For example, you can use a secure password generator to create a string with a mix of uppercase, lowercase, numbers, and special characters.

---

### Step 4: Start the Application

Open a terminal in the project root and run:

```bash
./mvnw spring-boot:run
```

The API will run at:

```
https://localhost:8443
```

Accept the self-signed certificate if prompted.

---

## 4. Test Users and Roles

| Username                 | Password     | Role  |
|--------------------------|--------------|-------|
| maki_admin               | maki_admin   | ADMIN |
| regular_user@example.com | regular_user | USER  |

These users are automatically created in `DataInitializer.java` when the application starts.

---

## 5. Postman Collection

The Postman collection is included as `makibeans.postman_collection.json`.  
You'll find it in the `/postman/` subfolder of the project. This collection contains a script that logs in as the admin
user and stores the resulting JWT token as api_key in the Postman environment. This token can then be used to
authenticate and try out the API endpoints that require authorization.

### To import:

1. Open Postman
2. Click `Import`
3. Select the JSON file

---

## 6. REST Endpoints and JSON Examples

The following examples show a selection of commonly used endpoints.  
For the full and most up-to-date list of available routes, required fields, and access permissions, refer to the Swagger
API documentation (see section 7).

### Authentication

```
POST /auth/register
```

```json
{
  "email": "newuser@example.com",
  "password": "user123"
}
```

```
POST /auth/login
```

```json
{
  "email": "user@example.com",
  "password": "user123"
}
```

*Response includes a JWT token.*

---

### Products

```
GET /products
```

*(Public - no token required)*  
Returns a list of products including variants, prices, images and attributes.

```json
[
  {
    "id": 1,
    "name": "Espresso beans",
    "description": "Rich and dark roast",
    "priceInCents": 999,
    "category": {
      "id": 1,
      "name": "Coffee"
    },
    "variants": [
      {
        "id": 1,
        "size": "250g",
        "priceInCents": 999,
        "stock": 50
      },
      {
        "id": 2,
        "size": "1kg",
        "priceInCents": 2999,
        "stock": 20
      }
    ],
    "attributes": [
      {
        "templateName": "Origin",
        "value": "Colombia"
      },
      {
        "templateName": "Roast",
        "value": "Dark"
      }
    ]
  }
]
```

```
POST /products
```

*(Requires ADMIN role)*  
Adds a new product:

```json
{
  "name": "Espresso Bonen",
  "description": "Intens en krachtig",
  "categoryId": 1
}
```

---

### Upload Product Image

```
POST /products/{id}/image
```

*(multipart/form-data, requires ADMIN token)*

---

### Filtering and Searching

```
GET /products?query=espresso&sort=price&order=asc
```

---

## 7. Swagger API Documentation

The full API documentation is available via Swagger (OpenAPI).

Once the application is running, visit:

```
https://localhost:8443/swagger-ui.html
```

There you'll find:

- All available endpoints
- HTTP methods (GET, POST, PUT, DELETE)
- Required and optional parameters
- Possible responses
- JWT authorization settings

Swagger also allows you to test endpoints live in the browser.

---

## 8. Running and Building the Project

### Using the Maven Wrapper (recommended)

You can start the project using the Maven wrapper (`mvnw` or `mvnw.cmd`). This allows the project to run without
requiring Maven to be installed system-wide.

Example:

```bash
./mvnw spring-boot:run
```

Windows:

```cmd
mvnw.cmd spring-boot:run
```

---

### Build JAR (packaged application)

With wrapper:

```bash
./mvnw package
```

Without wrapper:

```bash
mvn package
```

---

### Run Application

With wrapper:

```bash
./mvnw spring-boot:run
```

Without wrapper:

```bash
mvn spring-boot:run
```

---

### Drop Database (optional)

```sql
DROP DATABASE makibeans;
DROP USER <db_username>;
```

---

### Build Project without Running

With wrapper:

```bash
./mvnw clean install
```

Without wrapper:

```bash
mvn clean install
```

---

> Note: Ensure the `.env` file is correctly filled in and that your certificate is present in `resources/` if using
> HTTPS. For testing purposes, you can temporarily disable SSL in `application.properties`.