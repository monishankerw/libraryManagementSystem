package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.BorrowedBookDTO;
import com.libraryManagementSystem.service.BorrowBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/borrow-records")
public class BorrowRecordController {

    private final BorrowBookService borrowBookService;

    @PostMapping
    public ResponseEntity<BorrowedBookDTO> borrowBook(@RequestBody BorrowedBookDTO borrowedBookDTO) {
        return ResponseEntity.ok(borrowBookService.borrowBook(borrowedBookDTO));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowedBookDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowBookService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowedBookDTO>> getAllBorrowRecords() {
        return ResponseEntity.ok(borrowBookService.getAllBorrowRecords());
    }
}
