To build a basic Library Management System in Java that supports adding, updating, deleting, searching, and borrowing books, we will use a combination of object-oriented principles, data structures (like `ArrayList`), and file handling to store the data persistently.

### Step-by-Step Plan:
1. **Book Class**: Represents a book with attributes like title, author, ISBN, etc.
2. **Library Class**: Manages the collection of books with features for adding, updating, deleting, searching, and borrowing books.
3. **File Handling**: Use file I/O to persist the data of books so the library data is saved even after the program terminates.
4. **Main Class**: The entry point for the application that presents a menu-driven interface for users to interact with the system.

### 1. `Book` Class

```java
import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String isbn;
    private boolean isBorrowed;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isBorrowed = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", ISBN: " + isbn + ", Borrowed: " + isBorrowed;
    }
}
```

### 2. `Library` Class

This class manages the collection of books using an `ArrayList` and handles adding, updating, deleting, searching, borrowing books, and file handling.

```java
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    private ArrayList<Book> books;
    private final String filePath = "library_books.dat";

    public Library() {
        books = new ArrayList<>();
        loadBooksFromFile();
    }

    // Add a new book to the library
    public void addBook(Book book) {
        books.add(book);
        saveBooksToFile();
    }

    // Update a book's details
    public void updateBook(String isbn, String newTitle, String newAuthor) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                books.remove(book);
                books.add(new Book(newTitle, newAuthor, isbn));
                saveBooksToFile();
                return;
            }
        }
        System.out.println("Book with ISBN: " + isbn + " not found.");
    }

    // Delete a book from the library
    public void deleteBook(String isbn) {
        books.removeIf(book -> book.getIsbn().equals(isbn));
        saveBooksToFile();
    }

    // Search for a book by title
    public void searchBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                System.out.println(book);
                return;
            }
        }
        System.out.println("Book with title: " + title + " not found.");
    }

    // Borrow a book by ISBN
    public void borrowBook(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                if (!book.isBorrowed()) {
                    book.setBorrowed(true);
                    System.out.println("You have borrowed the book: " + book.getTitle());
                    saveBooksToFile();
                    return;
                } else {
                    System.out.println("The book is already borrowed.");
                    return;
                }
            }
        }
        System.out.println("Book with ISBN: " + isbn + " not found.");
    }

    // Save books to a file
    private void saveBooksToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load books from a file
    private void loadBooksFromFile() {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
                books = (ArrayList<Book>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Display all books
    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available in the library.");
        } else {
            books.forEach(System.out::println);
        }
    }
}
```

### 3. `Main` Class

This class will have a menu-driven interface for interacting with the Library.

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Search Book by Title");
            System.out.println("5. Borrow Book");
            System.out.println("6. Display All Books");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // consume the newline

            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter book ISBN: ");
                    String isbn = scanner.nextLine();
                    library.addBook(new Book(title, author, isbn));
                    break;
                case 2:
                    System.out.print("Enter ISBN of the book to update: ");
                    String updateIsbn = scanner.nextLine();
                    System.out.print("Enter new title: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter new author: ");
                    String newAuthor = scanner.nextLine();
                    library.updateBook(updateIsbn, newTitle, newAuthor);
                    break;
                case 3:
                    System.out.print("Enter ISBN of the book to delete: ");
                    String deleteIsbn = scanner.nextLine();
                    library.deleteBook(deleteIsbn);
                    break;
                case 4:
                    System.out.print("Enter book title to search: ");
                    String searchTitle = scanner.nextLine();
                    library.searchBookByTitle(searchTitle);
                    break;
                case 5:
                    System.out.print("Enter ISBN of the book to borrow: ");
                    String borrowIsbn = scanner.nextLine();
                    library.borrowBook(borrowIsbn);
                    break;
                case 6:
                    library.displayBooks();
                    break;
                case 0:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }
}
```

### Key Features:
- **File Handling**: Books are saved and loaded from a file (`library_books.dat`) to persist library data.
- **Data Structure**: The system uses an `ArrayList` to manage the book collection.
- **Borrow Functionality**: A simple borrowing system where you mark a book as borrowed.
- **Menu System**: A user-friendly menu to navigate through the system.

### To Run the Program:
1. Compile all the classes (`Book.java`, `Library.java`, `Main.java`).
2. Run the `Main` class to start the Library Management System.
3. Follow the on-screen menu to interact with the system.