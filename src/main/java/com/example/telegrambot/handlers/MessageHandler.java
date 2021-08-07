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

    public MessageHandler(MessageSender messageSender, KeyboardHandler keyboardHandler, Cache cache, Lesson lesson) {
        this.messageSender = messageSender;
        this.keyboardHandler = keyboardHandler;
        this.cache = cache;
        this.lesson = lesson;
        this.lesson.setPosition(Position.NONE);
    }

    public void outputDayLessons(String day, Iterable<Lesson> lessons, StringBuilder sb){
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
        if(message.hasText()){
            SendMessage sm = new SendMessage();
            if(lesson.getPosition() != null) {

                switch (lesson.getPosition()) {

                    // Positions for filling in data about a new lesson
                    case INPUT_LESSON -> {
                        cache.setLessonTitle(message.getText());
                        sm.setText("Выберите формат пары:");
                        lesson.setPosition(Position.INPUT_FORMAT);
                    }
                    case INPUT_FORMAT -> {
                        cache.setFormat(message.getText());
                        sm.setText("Выберите день:");
                        lesson.setPosition(Position.INPUT_DAY);
                    }
                    case INPUT_DAY -> {
                        cache.setDay(message.getText());
                        sm.setText("Введите имя преподавателя:");
                        lesson.setPosition(Position.INPUT_TEACHER);
                    }
                    case INPUT_TEACHER -> {
                        cache.setTeacherName(message.getText());
                        sm.setText("Вставьте ссылку на пару:");
                        lesson.setPosition(Position.INPUT_LINK);
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
                        lesson.setPosition(Position.NONE);
                        sm.setText("Предмет был добавлен в список");
                    }

                    // Position for selecting an item to remove
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
                        lesson.setPosition(Position.NONE);
                        sm.setText("Предмет был удалён из списка");
                    }

                    // Position to display the day's activities
                    case LEARN_THE_LESSONS_OF_THE_DAY -> {
                        Iterable<Lesson> lessons = postRepository.findAll();
                        StringBuilder sb = new StringBuilder();
                        outputDayLessons(message.getText(), lessons, sb);
                        lesson.setPosition(Position.NONE);
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
                sm.setText("Введите день, чтобы узнать пары: ");
            }


            sm.setChatId(String.valueOf(message.getChatId()));
            keyboardHandler.choose(sm);
            messageSender.sendMessage(sm);
        }
    }
}
