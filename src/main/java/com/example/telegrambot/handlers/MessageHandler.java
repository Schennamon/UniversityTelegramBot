package com.example.telegrambot.handlers;

import com.example.telegrambot.cache.Cache;
import com.example.telegrambot.domain.Lesson;
import com.example.telegrambot.domain.Position;
import com.example.telegrambot.domain.User;
import com.example.telegrambot.messagesender.MessageSender;
import com.example.telegrambot.repository.PostRepository;
import com.example.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
public class MessageHandler implements Handler<Message>{

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MessageSender messageSender;
    private final KeyboardHandler keyboardHandler;
    private final Lesson lesson;
    private final Cache cache;

    public MessageHandler(MessageSender messageSender, KeyboardHandler keyboardHandler, Cache cache, Lesson lesson, PostRepository postRepository, UserRepository userRepository) {
        this.messageSender = messageSender;
        this.keyboardHandler = keyboardHandler;
        this.cache = cache;
        this.lesson = lesson;
        this.lesson.setPosition(Position.NONE);
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    private void registrationUser(Message message){
        User user = new User();
        user.setUsername(message.getFrom().getUserName());
        user.setId(message.getChatId());
        user.setPosition(Position.NONE);
        userRepository.save(user);
    }

    private void outputDayLessons(String day, Iterable<Lesson> lessons, StringBuilder sb){
        int count =1;
        sb.append("\n" + day + ":\n");
        for(Lesson printLesson : lessons){
            if (printLesson.getDay().equals(day)) {
                sb.append("\t" + count + ". " + printLesson.getLesson() + ", ");
                sb.append(printLesson.getFormat() + ", ");
                sb.append(printLesson.getTeacherName() + ", ");
                sb.append(printLesson.getLink() + "\n");
                count++;
            }
        }
    }

    @Override
    public void choose(Message message) {
        if(!userRepository.existsById(message.getChatId())){
            registrationUser(message);
        }

        Optional<User> userOptional = userRepository.findById(message.getChatId());
        User user = userOptional.orElseGet(User::new);
        if (message.hasText()) {
            SendMessage sm = new SendMessage();
            if (user.getPosition() != null && user.getPosition() != Position.NONE) {
                switch (user.getPosition()) {

                    // Positions for filling in data about a new lesson
                    case INPUT_LESSON -> {
                        cache.setLessonTitle(message.getText());
                        user.setPosition(Position.INPUT_FORMAT);
                        userRepository.save(user);
                        sm.setText("Выберите формат пары:");
                    }
                    case INPUT_FORMAT -> {
                        cache.setFormat(message.getText());
                        user.setPosition(Position.INPUT_DAY);
                        userRepository.save(user);
                        sm.setText("Выберите день:");
                    }
                    case INPUT_DAY -> {
                        cache.setDay(message.getText());
                        user.setPosition(Position.INPUT_TEACHER);
                        userRepository.save(user);
                        sm.setText("Введите имя преподавателя:");
                    }
                    case INPUT_TEACHER -> {
                        cache.setTeacherName(message.getText());
                        user.setPosition(Position.INPUT_LINK);
                        userRepository.save(user);
                        sm.setText("Вставьте ссылку на пару:");
                    }
                    case INPUT_LINK -> {
                        cache.setLink(message.getText());
                        Lesson newLesson = new Lesson();
                        newLesson.setLesson(cache.getLessonTitle());
                        newLesson.setFormat(cache.getFormat());
                        newLesson.setDay(cache.getDay());
                        newLesson.setTeacherName(cache.getTeacherName());
                        newLesson.setLink(cache.getLink());
                        postRepository.save(newLesson);
                        user.setPosition(Position.NONE);
                        userRepository.save(user);
                        sm.setText("Предмет был добавлен в список");
                    }

                    // Position for selecting an item to remove
                    case INPUT_NUMBER_FOR_REMOVE -> {
                        int count = 1;
                        HashMap<Integer, Long> hashMap = new HashMap<>();
                        var num = Integer.parseInt(message.getText());
                        Iterable<Lesson> lessons = postRepository.findAll();
                        for (Lesson printLesson : lessons) {
                            hashMap.put(count, printLesson.getId());
                            count++;
                        }
                        Lesson lessonRemove = postRepository.findById(hashMap.get(num)).orElseThrow();
                        postRepository.delete(lessonRemove);
                        user.setPosition(Position.NONE);
                        userRepository.save(user);
                        sm.setText("Предмет был удалён из списка");
                    }

                    // Position to display the day's activities
                    case LEARN_THE_LESSONS_OF_THE_DAY -> {
                        Iterable<Lesson> lessons = postRepository.findAll();
                        StringBuilder sb = new StringBuilder();
                        outputDayLessons(message.getText(), lessons, sb);
                        user.setPosition(Position.NONE);
                        userRepository.save(user);
                        sm.setText(sb.toString());
                    }
                }
            }

            if (message.getText().equals("/start")) {
                sm.setText("Welcome to bot!");
            }
            if (message.getText().equals("/add")) {
                if(user.isStatus()) {
                    user.setPosition(Position.INPUT_LESSON);
                    userRepository.save(user);
                    sm.setText("Введите предмет: ");
                } else{
                    sm.setText("Вы не обладаете правами администратора для выполнения данной процедуры.");
                }
            }
            if (message.getText().equals("/all")) {
                Iterable<Lesson> lessons = postRepository.findAll();
                StringBuilder sb = new StringBuilder();

                outputDayLessons("Понедельник", lessons, sb);
                outputDayLessons("Вторник", lessons, sb);
                outputDayLessons("Среда", lessons, sb);
                outputDayLessons("Четверг", lessons, sb);
                outputDayLessons("Пятница", lessons, sb);
                outputDayLessons("Суббота", lessons, sb);

                sm.setText(sb.toString());
            }
            if (message.getText().equals("/remove")) {
                if(user.isStatus()) {
                    int count = 1;
                    var sb = new StringBuilder();
                    Iterable<Lesson> lessons = postRepository.findAll();
                    user.setPosition(Position.INPUT_NUMBER_FOR_REMOVE);
                    userRepository.save(user);

                    sb.append("Выберите пару, которую хотите удалить: \n");
                    for (Lesson printLesson : lessons) {
                        sb.append(count + ". " + printLesson.getLesson() + "\n");
                        count++;
                    }
                    sm.setText(sb.toString());
                } else {
                    sm.setText("Вы не обладаете правами администратора для выполнения данной процедуры.");
                }
            }
            if (message.getText().equals("/day")) {
                user.setPosition(Position.LEARN_THE_LESSONS_OF_THE_DAY);
                userRepository.save(user);
                sm.setText("Введите день, чтобы узнать пары: ");
            }

            sm.setChatId(String.valueOf(message.getChatId()));
            keyboardHandler.choose(sm, user);
            messageSender.sendMessage(sm);
        }
    }
}
