package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends BaseRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);

   // @Query("SELECT a FROM Account a WHERE a.isOnline = :isOnline")
   // List<Account> findByIsOnline(@Param("isOnline") boolean isOnline);
}
