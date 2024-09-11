package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookDTO;
import com.libraryManagementSystem.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO addUser(UserDTO userDTO);
    public List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);


}
