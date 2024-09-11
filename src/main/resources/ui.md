To provide a visual representation of how the UI screens might look for adding and managing books in the Library Management System, I'll describe the key screens and their layout in Markdown format. Since Markdown doesn't support complex UI design directly, I'll describe the layout and functionality in text. For actual visual mockups, you would use design tools or a frontend framework.

### **UI Screens for Library Management System**

---

#### **1. Home Screen**

The Home Screen serves as the landing page and navigation hub for the Library Management System.

```markdown
# Home Screen

+---------------------------------------------------------+
|                  **Library Management System**         |
+---------------------------------------------------------+
|                                                         |
|   [Add New Book]  [Manage Books]  [Borrow Books]       |
|                                                         |
|   [Search Books]  [View Borrowed Books]                |
|                                                         |
+---------------------------------------------------------+
```

- **Add New Book**: Navigates to the Add Book form.
- **Manage Books**: Displays a list of books with options to update or delete.
- **Borrow Books**: Allows users to borrow books.
- **Search Books**: Provides a search interface to find specific books.
- **View Borrowed Books**: Shows a list of currently borrowed books.

---

#### **2. Add New Book Screen**

This screen allows users to input details of a new book.

```markdown
# Add New Book

+---------------------------------------------------------+
|                  **Add New Book**                       |
+---------------------------------------------------------+
|                                                         |
|   **Title:**                [______________]           |
|   **Author:**               [______________]           |
|   **ISBN:**                 [______________]           |
|   **Published Date:**       [______/______/______]     |
|   **Genre:**                [______________]           |
|                                                         |
|   [Save]  [Cancel]                                    |
|                                                         |
+---------------------------------------------------------+
```

- **Title**: Input field for the book title.
- **Author**: Input field for the author's name.
- **ISBN**: Input field for the ISBN number.
- **Published Date**: Date picker for selecting the book's publication date.
- **Genre**: Dropdown or input field for specifying the genre.
- **Save**: Submits the form to add the new book.
- **Cancel**: Returns to the previous screen without saving.

---

#### **3. Manage Books Screen**

Displays a list of books with options to update or delete each entry.

```markdown
# Manage Books

+---------------------------------------------------------+
|                    **Manage Books**                     |
+---------------------------------------------------------+
|                                                         |
|   **Search:** [______________] [Search Button]         |
|                                                         |
|   +----------------+----------------+----------------+   |
|   | Title          | Author         | Genre          |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 1   | Author Name 1  | Genre Name 1   |   |
|   | [Update] [Delete]                |                |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 2   | Author Name 2  | Genre Name 2   |   |
|   | [Update] [Delete]                |                |   |
|   +----------------+----------------+----------------+   |
|                                                         |
+---------------------------------------------------------+
```

- **Search**: Allows filtering of the list of books.
- **Update**: Button to edit book details.
- **Delete**: Button to remove the book from the list.

---

#### **4. Borrow Books Screen**

Provides an interface for users to borrow books.

```markdown
# Borrow Books

+---------------------------------------------------------+
|                    **Borrow Books**                     |
+---------------------------------------------------------+
|                                                         |
|   **Search Book to Borrow:** [______________] [Search]  |
|                                                         |
|   +----------------+----------------+----------------+   |
|   | Title          | Author         | Available      |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 1   | Author Name 1  | Yes            |   |
|   | [Borrow]                            |                |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 2   | Author Name 2  | No             |   |
|   | [Borrow]                            |                |   |
|   +----------------+----------------+----------------+   |
|                                                         |
+---------------------------------------------------------+
```

- **Search Book to Borrow**: Input field to search for books that can be borrowed.
- **Borrow**: Button to initiate the borrowing process.

---

#### **5. View Borrowed Books Screen**

Shows a list of books currently borrowed by the user.

```markdown
# View Borrowed Books

+---------------------------------------------------------+
|                  **View Borrowed Books**                |
+---------------------------------------------------------+
|                                                         |
|   +----------------+----------------+----------------+   |
|   | Title          | Author         | Borrowed Date  |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 1   | Author Name 1  | Date           |   |
|   +----------------+----------------+----------------+   |
|   | Book Title 2   | Author Name 2  | Date           |   |
|   +----------------+----------------+----------------+   |
|                                                         |
+---------------------------------------------------------+
```

- **Title**: The name of the borrowed book.
- **Author**: The author of the borrowed book.
- **Borrowed Date**: The date on which the book was borrowed.

---

These screens provide a basic structure for the Library Management System. They can be further enhanced with Angular components and styled using CSS to match your design requirements.