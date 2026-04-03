package com.library.demo5.repository;

import java.util.Optional;

import com.library.demo5.entity.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUsernameIgnoreCase(String username);
    Optional<AppUser> findByUsernameIgnoreCase(String username);
}
