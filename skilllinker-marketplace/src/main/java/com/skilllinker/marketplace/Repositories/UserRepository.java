package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.User;
import com.skilllinker.marketplace.Enums.Role;
import com.skilllinker.marketplace.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);


    Optional<User> findById(Long id);


    List<User> findByRole(Role role);


    List<User> findByStatus(Status status);


    boolean existsByEmail(String email);
}
