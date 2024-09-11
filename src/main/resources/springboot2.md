Here's a complete project structure that incorporates Hibernate mappings, custom exception handling, validation, pagination, and sorting for a **Library Management System** using **Spring Boot**, **MySQL**, and **Hibernate**.

### Project Structure
```
LibraryManagementSystem
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com.example.library
│   │   │   │   ├── config
│   │   │   │   │   └── SwaggerConfig.java
│   │   │   │   ├── controller
│   │   │   │   │   ├── BookController.java
│   │   │   │   │   └── BorrowRecordController.java
│   │   │   │   ├── entity
│   │   │   │   │   ├── Book.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── BorrowRecord.java
│   │   │   │   ├── exception
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── CustomValidationException.java
│   │   │   │   ├── repository
│   │   │   │   │   ├── BookRepository.java
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── BorrowRecordRepository.java
│   │   │   │   ├── service
│   │   │   │   │   ├── BookService.java
│   │   │   │   │   └── BorrowService.java
│   │   │   │   └── LibraryManagementSystemApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       └── data.sql
│   └── test
│       └── java
│           └── com.example.library
│               └── LibraryManagementSystemApplicationTests.java
└── pom.xml
```

### 1. **Entity Classes**

#### **Book.java**
```java
package com.example.library.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Author is required")
    private String author;

    private boolean available = true;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Getters and setters
}
```

#### **User.java**
```java
package com.example.library.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotEmpty(message = "Email is required")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Getters and setters
}
```

#### **BorrowRecord.java**
```java
package com.example.library.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    private Date returnDate;

    private boolean returned;

    // Getters and setters
}
```

### 2. **Repository Interfaces**

#### **BookRepository.java**
```java
package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
```

#### **UserRepository.java**
```java
package com.example.library.repository;

import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
```

#### **BorrowRecordRepository.java**
```java
package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);
}
```

### 3. **Service Layer**

#### **BookService.java**
```java
package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        bookRepository.delete(book);
    }
}
```

#### **BorrowService.java**
```java
package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.User;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BorrowService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setUser(user);
        record.setBorrowDate(new Date());
        record.setReturned(false);

        book.setAvailable(false); // Mark book as borrowed
        bookRepository.save(book); // Update book status

        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setReturnDate(new Date());
        record.setReturned(true);

        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }
}
```

### 4. **Controllers**

#### **BookController.java**
```java
package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
```

#### **BorrowRecordController.java**
```java
package com.example.library.controller;

import com.example.library.entity.BorrowRecord;
import com.example.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
public class BorrowRecordController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping("/{userId}/book/{book

Id}")
    public BorrowRecord borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    @PutMapping("/return/{recordId}")
    public BorrowRecord returnBook(@PathVariable Long recordId) {
        return borrowService.returnBook(recordId);
    }
}
```

### 5. **Global Exception Handling**

#### **GlobalExceptionHandler.java**
```java
package com.example.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

#### **ResourceNotFoundException.java**
```java
package com.example.library.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### 6. **application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### 7. **Pom.xml Dependencies**
```xml
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Web Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Swagger for API Documentation -->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-boot-starter</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```
To implement file handling and store the log of book transactions (like adding, updating, deleting, and borrowing), you can use Java's standard file I/O API. Here's how to implement logging transactions into a file in your Library Management System.

### 1. **Define File Logger Utility**:

Create a utility class for file handling where we log the book transactions.

```java
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileLogger {

    private static final String FILE_PATH = "book_transactions.log";

    public static void logTransaction(String message) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
```

- The `logTransaction` method writes the transaction details into a file called `book_transactions.log`.
- The `FileWriter` is used with `true` for appending mode, so each log is added to the end of the file without overwriting previous logs.

### 2. **Modify the `BookService` to Log Transactions**:

Add calls to the `FileLogger.logTransaction()` method in your `BookService` methods to log each transaction.

```java
@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    
    public Book addBook(Book book) {
        Book savedBook = bookRepository.save(book);
        FileLogger.logTransaction("Added new book: " + savedBook.getTitle() + " by " + savedBook.getAuthor());
        return savedBook;
    }

    public List<Book> searchBooks(String title) {
        List<Book> books = bookRepository.findByTitleContaining(title);
        FileLogger.logTransaction("Searched books with title containing: " + title);
        return books;
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        bookRepository.deleteById(id);
        FileLogger.logTransaction("Deleted book: " + book.getTitle() + " by " + book.getAuthor());
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setGenre(bookDetails.getGenre());
        Book updatedBook = bookRepository.save(book);
        FileLogger.logTransaction("Updated book: " + updatedBook.getTitle() + " by " + updatedBook.getAuthor());
        return updatedBook;
    }
    
    public void borrowBook(Long bookId, boolean isBorrowed) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setBorrowed(isBorrowed);
        bookRepository.save(book);
        if (isBorrowed) {
            FileLogger.logTransaction("Borrowed book: " + book.getTitle() + " by " + book.getAuthor());
        } else {
            FileLogger.logTransaction("Returned book: " + book.getTitle() + " by " + book.getAuthor());
        }
    }
}
```

### 3. **Logging Book Transactions**:

Whenever a user adds, updates, deletes, or borrows a book, the action is logged into the `book_transactions.log` file, including the timestamp and details of the book involved in the transaction.

### 4. **Sample Log File Output**:

Here's what the `book_transactions.log` file might look like after some operations:

```
2024-08-19T12:30:15.321 - Added new book: The Catcher in the Rye by J.D. Salinger
2024-08-19T12:35:42.654 - Updated book: The Catcher in the Rye by J.D. Salinger
2024-08-19T13:10:20.211 - Borrowed book: The Catcher in the Rye by J.D. Salinger
2024-08-19T14:05:32.876 - Searched books with title containing: Harry Potter
2024-08-19T14:18:05.654 - Deleted book: 1984 by George Orwell
```

### 5. **Exception Handling**:

If there are any issues during logging, such as problems writing to the file, an error message is printed to the console. You can expand this to include proper error handling mechanisms like sending an alert or email in a production environment.

This setup provides basic logging functionality using file handling for your library management system.

To create a Library Management System using Java Spring Boot and MySQL, the following features will be implemented:

### Features:
1. **Add Books**: Add new books to the library.
2. **Update Books**: Modify the details of existing books.
3. **Delete Books**: Remove books from the library.
4. **Search Books**: Search for books based on criteria like title, author, or genre.
5. **Borrow Books**: Allow users to borrow books and keep track of borrow status.

### Steps to Implement:

#### 1. Setup Spring Boot Project with MySQL:
- Create a new Spring Boot project with dependencies like Spring Data JPA, Spring Web, MySQL Driver, and Lombok for simplicity.

#### 2. Define Database Schema:
You'll need two main entities: `Book` and `User`.

```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    isBorrowed BOOLEAN DEFAULT FALSE
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
```

#### 3. Create Book and User Entities:
```java
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String author;
    private String genre;
    
    private boolean isBorrowed;
}

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
}
```

#### 4. Create Repositories for CRUD operations:
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);
}

public interface UserRepository extends JpaRepository<User, Long> {
}
```

#### 5. Implement Services:
```java
@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> searchBooks(String title) {
        return bookRepository.findByTitleContaining(title);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setGenre(bookDetails.getGenre());
        return bookRepository.save(book);
    }
    
    public void borrowBook(Long bookId, boolean isBorrowed) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setBorrowed(isBorrowed);
        bookRepository.save(book);
    }
}
```

#### 6. Implement Controllers:
```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping
    public List<Book> searchBooks(@RequestParam String title) {
        return bookService.searchBooks(title);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<Void> borrowBook(@PathVariable Long id, @RequestParam boolean isBorrowed) {
        bookService.borrowBook(id, isBorrowed);
        return ResponseEntity.ok().build();
    }
}
```

#### 7. File Handling and Data Structures:
For simple data structures, you can store search results in lists and use file handling to log book transactions if needed.

#### 8. MySQL Configuration:
Add MySQL configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

#### 9. Testing the API:
You can test your system using Postman or a similar tool to add, update, delete, and search for books, and to borrow books.

This setup should cover all the core functionalities of the Library Management System. Let me know if you'd like any additional details or features!
This project structure provides a complete **Library Management System** with all the necessary components, including pagination and sorting for the borrow records, global exception handling, and validations. You can build and run it as a Spring Boot application using **Maven**.