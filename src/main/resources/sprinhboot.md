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
