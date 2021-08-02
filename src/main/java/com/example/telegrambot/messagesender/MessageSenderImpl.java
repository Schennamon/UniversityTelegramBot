package com.example.telegrambot.messagesender;

import com.example.telegrambot.MainBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessageSenderImpl implements MessageSender {

    private MainBot mainBot;

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            mainBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setMainBot(MainBot mainBot) {
        this.mainBot = mainBot;
    }
}
