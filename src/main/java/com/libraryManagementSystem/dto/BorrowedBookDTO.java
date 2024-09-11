package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private LocalDate borrowedDate;
    private Long userId;

}
