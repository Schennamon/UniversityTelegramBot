package com.example.telegrambot.repository;

import com.example.telegrambot.domain.Lesson;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Lesson, Long> {
}
