package com.example.telegrambot.repository;

import com.example.telegrambot.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
