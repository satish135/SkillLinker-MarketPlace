package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Enums.Role;
import com.skilllinker.marketplace.Enums.Status;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);

    List<UserResponse> getUserByRole(Role role);

    List<UserResponse> getUserByStatus(Status status);

    List<UserResponse> getAllUsers();

    void deleteById(Long id);
}

