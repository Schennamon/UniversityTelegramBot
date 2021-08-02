package com.example.telegrambot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class KeyboardHandler implements Handler<SendMessage>{

    @Override
    public void choose(SendMessage sm) {
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
