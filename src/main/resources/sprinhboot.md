To implement Hibernate mapping for the **Library Management System**, we will need to define the relationships between the different entities (e.g., `Book`, `User`, `BorrowRecord`, etc.) and set up proper mappings using annotations like `@OneToMany`, `@ManyToOne`, and `@JoinColumn`. Here’s a detailed example of how you can set up these mappings using Hibernate in Spring Boot.

### Entities and Hibernate Mapping

#### 1. **Book Entity**

This entity will represent the book in the system. A book can be borrowed by many users, but each borrow transaction is a separate record.

```java
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

    @ValidISBN
    private String isbn;

    private boolean available;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Constructors, Getters, Setters
}
```

#### 2. **User Entity**

A `User` can borrow many books, but each borrow record will be stored separately in the `BorrowRecord` entity.

```java
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Email is required")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Constructors, Getters, Setters
}
```

#### 3. **BorrowRecord Entity**

This entity represents the borrowing transaction, which is a many-to-one relationship with both `Book` and `User`. A user can borrow multiple books, and each book can be borrowed multiple times.

```java
import javax.persistence.*;
import java.util.Date;

@Entity
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    private Date returnDate;

    private boolean returned;

    // Constructors, Getters, Setters
}
```

### Hibernate Annotations Overview:
- `@Entity`: Marks the class as a JPA entity.
- `@Id`: Marks the primary key of the entity.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Specifies that the ID should be auto-generated.
- `@OneToMany`: Defines a one-to-many relationship. In the `Book` and `User` entities, this signifies that each book or user can have multiple borrow records.
- `@ManyToOne`: Defines a many-to-one relationship. In the `BorrowRecord` entity, this specifies the relation of `BorrowRecord` with `Book` and `User`.
- `@JoinColumn`: Specifies the foreign key column that refers to the associated entity.
- `@Temporal`: Specifies how the date is mapped in the database (for `Date` or `Calendar` fields).

### Example of Creating and Saving Data

Here’s a simple service method to demonstrate how you can handle borrowing a book using these mappings.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
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
            throw new RuntimeException("Book is already borrowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setUser(user);
        borrowRecord.setBorrowDate(new Date());
        borrowRecord.setReturned(false);

        book.setAvailable(false);  // Mark the book as borrowed
        bookRepository.save(book); // Save book status update

        return borrowRecordRepository.save(borrowRecord); // Save the borrow record
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        record.setReturnDate(new Date());
        record.setReturned(true);

        Book book = record.getBook();
        book.setAvailable(true); // Mark the book as available again
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }
}
```

### Pagination and Sorting (Additional Example)
To retrieve all the borrowed books for a user with pagination and sorting, you can modify the repository and service layers.

**Repository:**

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);
}
```

**Service:**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    public Page<BorrowRecord> getBorrowedBooksByUser(Long userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return borrowRecordRepository.findByUserId(userId, pageable);
    }
}
```

### Controller Example:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/user/{userId}")
    public Page<BorrowRecord> getBorrowedBooksByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy) {
        return borrowRecordService.getBorrowedBooksByUser(userId, page, size, sortBy);
    }
}
```

### Conclusion:
- **Book Entity**: Represents the books in the library.
- **User Entity**: Represents the users of the library.
- **BorrowRecord Entity**: Logs borrow transactions between books and users.
- **Pagination and Sorting**: Provides functionality to fetch borrowed books with pagination and sorting.
- **Hibernate Mappings**: Used `@OneToMany` and `@ManyToOne` relationships for proper entity mapping.



To convert the Java-based Library Management System into a **Spring Boot** application, we need to implement RESTful APIs for each of the functionalities (adding, updating, deleting, searching, and borrowing books). We will also use a **JPA** repository with **H2** as an in-memory database for simplicity.

### Step-by-Step Plan:
1. **Book Entity**: Represent the `Book` as an entity in the database.
2. **Book Repository**: Use Spring Data JPA to interact with the database.
3. **Book Service**: Implement business logic to add, update, delete, search, and borrow books.
4. **Book Controller**: Provide REST APIs for each operation.
5. **File Handling**: Optional, can be done using a simple database configuration or actual file handling in case you need to persist files separately.
6. **Main Application Class**: Standard Spring Boot `main()` class.

### Dependencies (in `pom.xml`)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 1. **Book Entity**

```java
package com.example.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private boolean isBorrowed;

    public Book() {}

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isBorrowed = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }
}
```

### 2. **Book Repository**

```java
package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByIsbn(String isbn);
    Book findByTitle(String title);
}
```

### 3. **Book Service**

The service layer handles business logic and interacts with the repository.

```java
package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Add a new book
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // Update book details
    public Book updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        return bookRepository.save(existingBook);
    }

    // Delete a book
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Search for a book by title
    public Book searchByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    // Borrow a book
    public Book borrowBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new RuntimeException("Book with ISBN: " + isbn + " not found");
        }
        if (!book.isBorrowed()) {
            book.setBorrowed(true);
            return bookRepository.save(book);
        } else {
            throw new RuntimeException("Book is already borrowed");
        }
    }

    // Return all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
```

### 4. **Book Controller**

The REST controller provides endpoints for each of the operations.

```java
package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(book));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Book> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    @PutMapping("/borrow")
    public ResponseEntity<Book> borrowBook(@RequestParam String isbn) {
        return ResponseEntity.ok(bookService.borrowBook(isbn));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
}
```

### 5. **Application Properties** (in `src/main/resources/application.properties`)

```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.path=/h2-console
```

### 6. **Main Application Class**

```java
package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
```

### Testing the APIs

1. **Add a book** (POST request)
   ```
   POST http://localhost:8080/api/books/add
   {
       "title": "Java Programming",
       "author": "James Gosling",
       "isbn": "1234567890"
   }
   ```

2. **Update a book** (PUT request)
   ```
   PUT http://localhost:8080/api/books/update/{id}
   {
       "title": "Advanced Java",
       "author": "James Gosling",
       "isbn": "1234567890"
   }
   ```

3. **Delete a book** (DELETE request)
   ```
   DELETE http://localhost:8080/api/books/delete/{id}
   ```

4. **Search a book by title** (GET request)
   ```
   GET http://localhost:8080/api/books/search?title=Java Programming
   ```

5. **Borrow a book** (PUT request)
   ```
   PUT http://localhost:8080/api/books/borrow?isbn=1234567890
   ```

6. **Get all books** (GET request)
   ```
   GET http://localhost:8080/api/books/all
   ```

---

This Spring Boot Library Management System allows adding, updating, deleting, searching, and borrowing books through REST APIs while managing the persistence of data using Spring Data JPA and H2 database.