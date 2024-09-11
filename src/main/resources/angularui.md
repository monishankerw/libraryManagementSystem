To create a complete Angular UI for the Library Management System, we'll need to set up the Angular components and services to interact with the APIs from the Spring Boot backend. Below is an outline and code for creating the Angular application, including the necessary components and services.

### 1. **Setup Angular Project**

First, create a new Angular project:

```bash
ng new library-management
cd library-management
```

### 2. **Install Dependencies**

Install Angular Material for UI components (optional but recommended for styling):

```bash
ng add @angular/material
```

### 3. **Create Angular Components**

Generate the components for the different screens:

```bash
ng generate component home
ng generate component add-book
ng generate component manage-books
ng generate component borrow-books
ng generate component view-borrowed-books
```

### 4. **Create Angular Services**

Generate a service to handle API interactions:

```bash
ng generate service book
```

### 5. **Configure Angular Routing**

Update `app-routing.module.ts` to define routes for the different components:

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AddBookComponent } from './add-book/add-book.component';
import { ManageBooksComponent } from './manage-books/manage-books.component';
import { BorrowBooksComponent } from './borrow-books/borrow-books.component';
import { ViewBorrowedBooksComponent } from './view-borrowed-books/view-borrowed-books.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'add-book', component: AddBookComponent },
  { path: 'manage-books', component: ManageBooksComponent },
  { path: 'borrow-books', component: BorrowBooksComponent },
  { path: 'view-borrowed-books', component: ViewBorrowedBooksComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

### 6. **Implement Angular Service**

Update `book.service.ts` to handle HTTP requests:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = 'http://localhost:8080/api/books'; // Change to your API URL

  constructor(private http: HttpClient) { }

  getBooks(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  addBook(book: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, book);
  }

  updateBook(book: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${book.id}`, book);
  }

  deleteBook(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  searchBooks(query: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search?query=${query}`);
  }

  borrowBook(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/borrow/${id}`, {});
  }

  getBorrowedBooks(): Observable<any> {
    return this.http.get(`${this.apiUrl}/borrowed`);
  }
}
```

### 7. **Implement Components**

#### **`home.component.html`**

```html
<h1>Library Management System</h1>
<nav>
  <a routerLink="/add-book">Add New Book</a>
  <a routerLink="/manage-books">Manage Books</a>
  <a routerLink="/borrow-books">Borrow Books</a>
  <a routerLink="/view-borrowed-books">View Borrowed Books</a>
</nav>
```

#### **`add-book.component.html`**

```html
<h2>Add New Book</h2>
<form (ngSubmit)="addBook()">
  <label for="title">Title:</label>
  <input id="title" [(ngModel)]="book.title" name="title" required>

  <label for="author">Author:</label>
  <input id="author" [(ngModel)]="book.author" name="author" required>

  <label for="isbn">ISBN:</label>
  <input id="isbn" [(ngModel)]="book.isbn" name="isbn" required>

  <label for="publishedDate">Published Date:</label>
  <input id="publishedDate" [(ngModel)]="book.publishedDate" name="publishedDate" type="date" required>

  <label for="genre">Genre:</label>
  <input id="genre" [(ngModel)]="book.genre" name="genre" required>

  <button type="submit">Save</button>
</form>
```

#### **`manage-books.component.html`**

```html
<h2>Manage Books</h2>
<input [(ngModel)]="searchQuery" placeholder="Search Books" />
<button (click)="searchBooks()">Search</button>

<table>
  <thead>
    <tr>
      <th>Title</th>
      <th>Author</th>
      <th>Genre</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let book of books">
      <td>{{ book.title }}</td>
      <td>{{ book.author }}</td>
      <td>{{ book.genre }}</td>
      <td>
        <button (click)="updateBook(book)">Update</button>
        <button (click)="deleteBook(book.id)">Delete</button>
      </td>
    </tr>
  </tbody>
</table>
```

#### **`borrow-books.component.html`**

```html
<h2>Borrow Books</h2>
<input [(ngModel)]="searchQuery" placeholder="Search Books" />
<button (click)="searchBooks()">Search</button>

<table>
  <thead>
    <tr>
      <th>Title</th>
      <th>Author</th>
      <th>Available</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let book of books">
      <td>{{ book.title }}</td>
      <td>{{ book.author }}</td>
      <td>{{ book.available ? 'Yes' : 'No' }}</td>
      <td>
        <button (click)="borrowBook(book.id)" [disabled]="!book.available">Borrow</button>
      </td>
    </tr>
  </tbody>
</table>
```

#### **`view-borrowed-books.component.html`**

```html
<h2>View Borrowed Books</h2>

<table>
  <thead>
    <tr>
      <th>Title</th>
      <th>Author</th>
      <th>Borrowed Date</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let book of borrowedBooks">
      <td>{{ book.title }}</td>
      <td>{{ book.author }}</td>
      <td>{{ book.borrowedDate }}</td>
    </tr>
  </tbody>
</table>
```

### 8. **Component Logic**

#### **`add-book.component.ts`**

```typescript
import { Component } from '@angular/core';
import { BookService } from '../book.service';

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css']
})
export class AddBookComponent {
  book = {
    title: '',
    author: '',
    isbn: '',
    publishedDate: '',
    genre: ''
  };

  constructor(private bookService: BookService) {}

  addBook() {
    this.bookService.addBook(this.book).subscribe(response => {
      alert('Book added successfully!');
      this.book = {
        title: '',
        author: '',
        isbn: '',
        publishedDate: '',
        genre: ''
      };
    });
  }
}
```

#### **`manage-books.component.ts`**

```typescript
import { Component, OnInit } from '@angular/core';
import { BookService } from '../book.service';

@Component({
  selector: 'app-manage-books',
  templateUrl: './manage-books.component.html',
  styleUrls: ['./manage-books.component.css']
})
export class ManageBooksComponent implements OnInit {
  books = [];
  searchQuery = '';

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.bookService.getBooks().subscribe(data => {
      this.books = data;
    });
  }

  searchBooks() {
    this.bookService.searchBooks(this.searchQuery).subscribe(data => {
      this.books = data;
    });
  }

  updateBook(book: any) {
    // Navigate to update form or implement update logic
  }

  deleteBook(id: number) {
    this.bookService.deleteBook(id).subscribe(() => {
      alert('Book deleted successfully!');
      this.loadBooks();
    });
  }
}
```

#### **`borrow-books.component.ts`**

```typescript
import { Component, OnInit } from '@angular/core';
import { BookService } from '../book.service';

@Component({
  selector: 'app-borrow-books',
  templateUrl: './borrow-books.component.html',
  styleUrls: ['./borrow-books.component.css']
})
export class BorrowBooksComponent implements OnInit {
  books = [];
  searchQuery = '';

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.bookService.get

Books().subscribe(data => {
      this.books = data;
    });
  }

  searchBooks() {
    this.bookService.searchBooks(this.searchQuery).subscribe(data => {
      this.books = data;
    });
  }

  borrowBook(id: number) {
    this.bookService.borrowBook(id).subscribe(() => {
      alert('Book borrowed successfully!');
      this.loadBooks();
    });
  }
}
```

#### **`view-borrowed-books.component.ts`**

```typescript
import { Component, OnInit } from '@angular/core';
import { BookService } from '../book.service';

@Component({
  selector: 'app-view-borrowed-books',
  templateUrl: './view-borrowed-books.component.html',
  styleUrls: ['./view-borrowed-books.component.css']
})
export class ViewBorrowedBooksComponent implements OnInit {
  borrowedBooks = [];

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBorrowedBooks();
  }

  loadBorrowedBooks() {
    this.bookService.getBorrowedBooks().subscribe(data => {
      this.borrowedBooks = data;
    });
  }
}
```

### 9. **Add HTTP Client Module**

Ensure `HttpClientModule` is added to `app.module.ts`:

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AddBookComponent } from './add-book/add-book.component';
import { ManageBooksComponent } from './manage-books/manage-books.component';
import { BorrowBooksComponent } from './borrow-books/borrow-books.component';
import { ViewBorrowedBooksComponent } from './view-borrowed-books/view-borrowed-books.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AddBookComponent,
    ManageBooksComponent,
    BorrowBooksComponent,
    ViewBorrowedBooksComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

This setup will give you a functional UI for your Library Management System, integrated with the backend API. You can further enhance and style the components as needed using Angular Material or custom CSS.