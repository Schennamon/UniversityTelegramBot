package com.example.telegrambot.handlers;

import com.example.telegrambot.domain.Position;
import com.example.telegrambot.domain.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class KeyboardHandler{
    public void choose(SendMessage sm, User user){
        if(user.getPosition().equals(Position.INPUT_DAY) || user.getPosition().equals(Position.SELECTION_OF_THE_DAY_FOR_INPUT)){
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
        else if(user.getPosition().equals(Position.INPUT_FORMAT)){
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
        else if(user.getPosition().equals(Position.INPUT_CHOICE_FOR_EDIT)){
            var markup = new ReplyKeyboardMarkup();
            var keyboardRows = new ArrayList<KeyboardRow>();
            var row1 = new KeyboardRow();
            var row2 = new KeyboardRow();
            row1.add("Имя");
            row1.add("Фамилия");
            row1.add("Дожлность");
            row2.add("Номер телефона");
            row2.add("Почту");
            keyboardRows.add(row1);
            keyboardRows.add(row2);
            markup.setKeyboard(keyboardRows);
            markup.setResizeKeyboard(true);
            sm.setReplyMarkup(markup);
        }
        else {
            var markup = new ReplyKeyboardMarkup();
            var keyboardRows = new ArrayList<KeyboardRow>();
            var row1 = new KeyboardRow();
            var row2 = new KeyboardRow();
            row1.add("/day");
            row1.add("/all");
            row1.add("/all_teachers");
            row2.add("/add");
            row2.add("/remove");
            row2.add("/add_teacher");
            row2.add("/remove_teacher");
            row2.add("/edit_teacher");
            keyboardRows.add(row1);
            keyboardRows.add(row2);
            markup.setKeyboard(keyboardRows);
            markup.setResizeKeyboard(true);
            sm.setReplyMarkup(markup);
        }
    }
}
