To include **custom validation** in the Library Management System, you need to define a custom annotation and its corresponding validator. This will allow you to validate custom conditions on the entity fields (e.g., checking that a book title is unique or that an email format is valid beyond default validation).

Here's how you can implement custom validation in the Library Management System:

### Steps to Implement Custom Validation:

1. **Create Custom Validation Annotation** (e.g., for checking unique book titles).
2. **Implement Validator Class** that contains the logic for the custom validation.
3. **Apply the Custom Annotation** to your entity fields.
4. **Handle validation messages** through exception handling.

### 1. **Custom Validation Annotation**

Create an annotation that will be applied to the fields that need custom validation.

#### **UniqueBookTitle.java**
```java
package com.example.library.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueBookTitleValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueBookTitle {
    String message() default "Book title must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

### 2. **Validator Class**

This class contains the logic that performs the actual validation. It checks whether the book title is unique in the database.

#### **UniqueBookTitleValidator.java**
```java
package com.example.library.validation;

import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueBookTitleValidator implements ConstraintValidator<UniqueBookTitle, String> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void initialize(UniqueBookTitle constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // This will be handled by @NotEmpty
        }
        return bookRepository.findByTitle(value) == null; // Valid if book title doesn't exist
    }
}
```

### 3. **Applying the Custom Validation in Entity**

Now you can apply the custom validation annotation (`@UniqueBookTitle`) to the `title` field of the `Book` entity.

#### **Book.java**
```java
package com.example.library.entity;

import com.example.library.validation.UniqueBookTitle;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title is required")
    @UniqueBookTitle // Custom validation annotation
    private String title;

    @NotEmpty(message = "Author is required")
    private String author;

    private boolean available = true;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Getters and setters
}
```

### 4. **Update Repository**

Ensure that the `BookRepository` contains a method to find a book by title to support the custom validation.

#### **BookRepository.java**
```java
package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitle(String title); // Custom query method to find by title
}
```

### 5. **Global Exception Handling**

The global exception handler already handles validation exceptions. Ensure it's updated to catch and respond to validation errors.

#### **GlobalExceptionHandler.java**
```java
package com.example.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
```

### 6. **Controller with Validation**

When creating or updating a book, the validation logic will automatically trigger.

#### **BookController.java**
```java
package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public Book addBook(@Valid @RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
```

### 7. **Testing Validation**

When you try to add a new book with a title that already exists, the validation will fail, and an error message will be returned:

```json
{
  "title": "Book title must be unique"
}
```

### Summary of the Features Included:
- **Custom Validation** using the `@UniqueBookTitle` annotation to ensure book titles are unique.
- **Global Exception Handling** for validation errors and resource not found exceptions.
- **Standard Validation** using annotations like `@NotEmpty` and `@Email`.
- **Integration with Hibernate** for the repository layer and database operations.

This structure implements **custom validation** in a clean and reusable way, along with the necessary changes to handle global validation errors in the Spring Boot Library Management System.