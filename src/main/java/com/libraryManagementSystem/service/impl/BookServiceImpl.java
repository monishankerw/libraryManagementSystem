package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BookDTO;
import com.libraryManagementSystem.entity.Book;
import com.libraryManagementSystem.exception.ResourceNotFoundException;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        log.info("Adding new book: {}", bookDTO);
        Book book = modelMapper.map(bookDTO, Book.class);
        bookRepository.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO getBookById(Long id) {
        log.info("Fetching book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        log.info("Updating book with ID: {} with data: {}", id, bookDTO);
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        modelMapper.map(bookDTO, existingBook);
        bookRepository.save(existingBook);
        return modelMapper.map(existingBook, BookDTO.class);
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        bookRepository.deleteById(id);
    }
}
