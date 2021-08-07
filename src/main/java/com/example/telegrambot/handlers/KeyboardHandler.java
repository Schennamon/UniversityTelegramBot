package com.example.telegrambot.handlers;

import com.example.telegrambot.domain.Lesson;
import com.example.telegrambot.domain.Position;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class KeyboardHandler implements Handler<SendMessage>{
    private final Lesson lesson;

    public KeyboardHandler(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public void choose(SendMessage sm) {
        if(lesson.getPosition().equals(Position.INPUT_DAY) || lesson.getPosition().equals(Position.LEARN_THE_LESSONS_OF_THE_DAY)){
            var markup = new ReplyKeyboardMarkup();
            var keyboardRows = new ArrayList<KeyboardRow>();
            var row1 = new KeyboardRow();
            var row2 = new KeyboardRow();
            row1.add("Понедельник");
            row1.add("Вторник");
            row1.add("Среда");
            row2.add("Четверг");
            row2.add("Пятница");
            row2.add("Суббота");
            keyboardRows.add(row1);
            keyboardRows.add(row2);
            markup.setKeyboard(keyboardRows);
            markup.setResizeKeyboard(true);
            sm.setReplyMarkup(markup);
        }
        else if(lesson.getPosition().equals(Position.INPUT_FORMAT)){
            var markup = new ReplyKeyboardMarkup();
            var keyboardRows = new ArrayList<KeyboardRow>();
            var row1 = new KeyboardRow();
            row1.add("Лекция");
            row1.add("Практика");
            keyboardRows.add(row1);
            markup.setKeyboard(keyboardRows);
            markup.setResizeKeyboard(true);
            sm.setReplyMarkup(markup);
        }
        else {
            var markup = new ReplyKeyboardMarkup();
            var keyboardRows = new ArrayList<KeyboardRow>();
            var row1 = new KeyboardRow();
            row1.add("/day");
            row1.add("/all");
            row1.add("/add");
            row1.add("/remove");
            keyboardRows.add(row1);
            markup.setKeyboard(keyboardRows);
            markup.setResizeKeyboard(true);
            sm.setReplyMarkup(markup);
        }
    }
}
