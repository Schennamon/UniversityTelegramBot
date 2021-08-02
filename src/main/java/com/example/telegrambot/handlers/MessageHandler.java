package com.example.telegrambot.handlers;

import com.example.telegrambot.cache.Cache;
import com.example.telegrambot.domain.Lesson;
import com.example.telegrambot.domain.Position;
import com.example.telegrambot.messagesender.MessageSender;
import com.example.telegrambot.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
public class MessageHandler implements Handler<Message>{

    @Autowired
    private PostRepository postRepository;

    private final MessageSender messageSender;
    private final KeyboardHandler keyboardHandler;
    private final Lesson lesson;
    private final Cache cache;

    public MessageHandler(MessageSender messageSender, KeyboardHandler keyboardHandler, Lesson lesson, Cache cache) {
        this.messageSender = messageSender;
        this.keyboardHandler = keyboardHandler;
        this.lesson = lesson;
        this.cache = cache;
    }

    @Override
    public void choose(Message message) {
        if(message.hasText()){
            SendMessage sm = new SendMessage();

            if(lesson.getPosition() != null) {
                switch (lesson.getPosition()) {
                    case INPUT_LESSON -> {
                        cache.setLessonTitle(message.getText());
                        sm.setText("Введите день пары:");
                        lesson.setPosition(Position.INPUT_DAY);
                    }
                    case INPUT_DAY -> {
                        cache.setDay(message.getText());
                        Lesson newLesson = new Lesson();
                        newLesson.setLesson(cache.getLessonTitle());
                        newLesson.setDay(cache.getDay());
                        postRepository.save(newLesson);
                        sm.setText("Предмет был добавлен в список");
                        lesson.setPosition(Position.NONE);
                    }
                    case INPUT_NUMBER_FOR_REMOVE -> {
                        int count = 1;
                        var hashMap = new HashMap();
                        var num = Integer.parseInt(message.getText());
                        Iterable<Lesson> lessons = postRepository.findAll();
                        for (Lesson printLesson : lessons) {
                            hashMap.put(count, printLesson.getId());
                            count++;
                        }
                        Lesson lessonRemove = postRepository.findById((Long) hashMap.get(num)).orElseThrow();
                        postRepository.delete(lessonRemove);
                        sm.setText("Предмет был удалён из списка");
                        lesson.setPosition(Position.NONE);
                    }
                    case LEARN_THE_LESSONS_OF_THE_DAY -> {
                        Iterable<Lesson> lessons = postRepository.findAll();
                        StringBuilder sb = new StringBuilder("Список дня:\n");
                        for (Lesson ls : lessons) {
                            if(ls.getDay().equals(message.getText())) {
                                sb.append(ls.getLesson() + "\n");
                            }
                        }
                        sm.setText(sb.toString());
                    }
                }
            }

            if (message.getText().equals("/start")) {
                sm.setText("Welcome to bot!");
            }
            if (message.getText().equals("/add")) {
                lesson.setPosition(Position.INPUT_LESSON);
                sm.setText("Введите предмет: ");
            }
            if (message.getText().equals("/all")) {
                int count = 1;
                Iterable<Lesson> lessons = postRepository.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("Список всех пар: \n");
                for (Lesson printLesson : lessons) {
                    sb.append(count + ". " + printLesson.getLesson() + "\n");
                    count++;
                }
                sm.setText(sb.toString());
            }
            if (message.getText().equals("/remove")) {
                int count = 1;
                var sb = new StringBuilder();
                Iterable<Lesson> lessons = postRepository.findAll();
                lesson.setPosition(Position.INPUT_NUMBER_FOR_REMOVE);

                sb.append("Выберите пару, которую хотите удалить: \n");
                for (Lesson printLesson : lessons) {
                    sb.append(count + ". " + printLesson.getLesson() + "\n");
                    count++;
                }
                sm.setText(sb.toString());
            }
            if (message.getText().equals("/day")) {
                lesson.setPosition(Position.LEARN_THE_LESSONS_OF_THE_DAY);
                messageSender.sendMessage(SendMessage.builder()
                        .text("Введите день, чтобы узнать пары: ")
                        .chatId(String.valueOf(message.getChatId()))
                        .build());
            }


            sm.setChatId(String.valueOf(message.getChatId()));
            keyboardHandler.choose(sm);
            messageSender.sendMessage(sm);
        }
    }
}
