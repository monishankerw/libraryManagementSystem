package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BorrowedBookDTO;
import com.libraryManagementSystem.entity.Book;
import com.libraryManagementSystem.entity.BorrowRecord;
import com.libraryManagementSystem.entity.User;
import com.libraryManagementSystem.exception.ResourceNotFoundException;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.repository.BorrowRecordRepository;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.service.BorrowBookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BorrowBookServiceImpl implements BorrowBookService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public BorrowedBookDTO borrowBook(BorrowedBookDTO borrowedBookDTO) {
        BorrowRecord borrowRecord = new BorrowRecord();

        // Fetch the User and Book entities before saving the borrow record
        User user = userRepository.findById(borrowedBookDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(borrowedBookDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // Set the user and book in the BorrowRecord
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(new Date());
        borrowRecord.setReturned(false);

        // Save the BorrowRecord
        borrowRecordRepository.save(borrowRecord);

        // Return the DTO
        return modelMapper.map(borrowRecord, BorrowedBookDTO.class);
    }

    @Override
    public BorrowedBookDTO returnBook(Long id) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found"));
        borrowRecord.setReturned(true);
        borrowRecordRepository.save(borrowRecord);
        return modelMapper.map(borrowRecord, BorrowedBookDTO.class);
    }

    @Override
    public List<BorrowedBookDTO> getAllBorrowRecords() {
        return borrowRecordRepository.findAll()
                .stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord, BorrowedBookDTO.class))
                .collect(Collectors.toList());
    }
}
