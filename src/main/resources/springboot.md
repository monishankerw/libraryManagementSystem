To complete the backend for the Library Management System using Spring Boot, we'll cover the following aspects:

1. **Entities**
2. **Controllers**
3. **Services**
4. **Exceptions**
5. **Custom Validators**
6. **Hibernate Mapping**
7. **Pagination and Sorting**

### 1. **Entities**

**Book.java**

```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Author is required")
    private String author;

    @NotEmpty(message = "ISBN is required")
    private String isbn;

    @NotNull(message = "Published Date is required")
    private LocalDate publishedDate;

    @NotEmpty(message = "Genre is required")
    private String genre;

    private boolean available = true; // Default to available

    // Getters and Setters
    // ...
}
```

**BorrowedBook.java**

```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class BorrowedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    private LocalDate borrowedDate;

    // Getters and Setters
    // ...
}
```

### 2. **Controllers**

**BookController.java**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;

    @GetMapping
    public Page<Book> getBooks(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortBy = sort[0];
        String direction = sort[1];
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return bookService.getBooks(pageable);
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) {
        return bookService.searchBooks(query);
    }

    @PostMapping("/borrow/{id}")
    public ResponseEntity<Void> borrowBook(@PathVariable Long id) {
        bookService.borrowBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/borrowed")
    public List<BorrowedBook> getBorrowedBooks() {
        return bookService.getBorrowedBooks();
    }
}
```

### 3. **Services**

**BookService.java**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setPublishedDate(book.getPublishedDate());
        existingBook.setGenre(book.getGenre());
        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCase(query);
    }

    public void borrowBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (!book.isAvailable()) {
            throw new BookAlreadyBorrowedException("Book already borrowed");
        }
        book.setAvailable(false);
        bookRepository.save(book);
        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setBook(book);
        borrowedBook.setBorrowedDate(LocalDate.now());
        borrowedBookRepository.save(borrowedBook);
    }

    public List<BorrowedBook> getBorrowedBooks() {
        return borrowedBookRepository.findAll();
    }
}
```

### 4. **Exceptions**

**GlobalExceptionHandler.java**

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBookAlreadyBorrowedException(BookAlreadyBorrowedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
```

**ResourceNotFoundException.java**

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**BookAlreadyBorrowedException.java**

```java
public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(String message) {
        super(message);
    }
}
```

### 5. **Custom Validators**

**BookValidator.java**

```java
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookValidator implements ConstraintValidator<ValidBook, Book> {

    @Override
    public boolean isValid(Book book, ConstraintValidatorContext context) {
        if (book.getPublishedDate().isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Published date cannot be in the future")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
```

**ValidBook.java**

```java
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BookValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBook {
    String message() default "Invalid book data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### 6. **Hibernate Mapping**

**`Book` and `BorrowedBook` Entities already include Hibernate mapping annotations.**

### 7. **Pagination and Sorting**

**BookRepository.java**

```java
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
}
```

**BorrowedBookRepository.java**

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
}
```

### Complete Project Structure

**`src/main/java/com/librarymanagement`**

- **controller**
    - BookController.java
- **exception**
    - GlobalExceptionHandler.java
    - ResourceNotFoundException.java
    - BookAlreadyBorrowedException.java
- **model**
    - Book.java
    - BorrowedBook.java
- **repository**
    - BookRepository.java
    - BorrowedBookRepository.java
- **service**
    - BookService.java
- **validator**
    - BookValidator.java
    - ValidBook.java

**`src/main/resources/application.properties`**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

This setup should give you a complete backend system for managing books in a library with Spring Boot.