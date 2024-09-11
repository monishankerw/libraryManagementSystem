package com.libraryManagementSystem.service;


import com.libraryManagementSystem.dto.BorrowedBookDTO;

import java.util.List;

public interface BorrowBookService {
    BorrowedBookDTO borrowBook(BorrowedBookDTO borrowedBookDTO);
    BorrowedBookDTO returnBook(Long id);
    List<BorrowedBookDTO> getAllBorrowRecords();
}
