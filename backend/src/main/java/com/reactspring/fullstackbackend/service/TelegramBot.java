package com.reactspring.fullstackbackend.service;

import com.reactspring.fullstackbackend.config.BotConfig;
import com.reactspring.fullstackbackend.model.User;
import com.reactspring.fullstackbackend.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private String userName;
    private String name;
    private String email;

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    List<UserStep> userStep = new ArrayList<>();
    List<User> userData = new ArrayList<>();

    public TelegramBot(BotConfig botConfig, UserRepository userRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = new Message();
        User user = new User();
        UserStep userStep = new UserStep();

        if (update.hasMessage()) {
            String chatId = update.getMessage().getChatId().toString();
            UserStep saveUser = saveUser(chatId);
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                try {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Iltimos tilni tanlang\n\nПожалуйста, выберите язык");
                    sendMessage.setChatId(chatId);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> column = new ArrayList<>();

                    InlineKeyboardButton buttonUZ = new InlineKeyboardButton();
                    buttonUZ.setText("\uD83C\uDDFA\uD83C\uDDFF UZ");
                    buttonUZ.setCallbackData(BotConstant.UZ_SELECTED);
                    column.add(buttonUZ);

                    InlineKeyboardButton buttonRU = new InlineKeyboardButton();
                    buttonRU.setText("\uD83C\uDDF7\uD83C\uDDFA RU");
                    buttonRU.setCallbackData(BotConstant.RU_SELECTED);
                    column.add(buttonRU);

                    List<List<InlineKeyboardButton>> row = new ArrayList<>();

                    row.add(column);

                    inlineKeyboardMarkup.setKeyboard(row);
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                    execute(sendMessage);
                    saveUser.setStep(BotConstant.SELECT_LANG);
                    System.out.println(saveUser.getStep());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (saveUser.getStep().equals(BotConstant.ENTER_NAME)) {
                name = text;
//                user.setUserName(text);
                if (saveUser.getLang().equals(BotConstant.RU_SELECTED)) {
                    sendMessageToClient(chatId, "Пожалуйста, введите адрес электронной почты");
                } else if (saveUser.getLang().equals(BotConstant.UZ_SELECTED)) {
                    sendMessageToClient(chatId, "Iltimos emailingizni kiriting");
                }
                saveUser.setStep(BotConstant.ENTER_EMAIL);
                System.out.println(saveUser.getStep());
            } else if (saveUser.getStep().equals(BotConstant.ENTER_EMAIL)) {
                if (saveUser.getLang().equals(BotConstant.RU_SELECTED)) {
                    sendMessageToClient(chatId, "Пожалуйста, введите ваш логин");
                    saveUser.setStep(BotConstant.ENTER_USERNAME);
                } else if (saveUser.getLang().equals(BotConstant.UZ_SELECTED)) {
                    sendMessageToClient(chatId, "Iltimos loginingizni kiriting");
                    saveUser.setStep(BotConstant.ENTER_USERNAME);
                }
                email = text;
                System.out.println(saveUser.getStep());
            } else if (saveUser.getStep().equals(BotConstant.ENTER_USERNAME)) {
//              user.setUserName(text);
                userName = text;
                saveUser.setStep(BotConstant.SHOW_DATA);
                System.out.println(saveUser.getStep());
                user.setName(name);
                user.setEmail(email);
                user.setUserName(userName);
                if (user.getName() != null && user.getEmail() != null && user.getUserName() != null) {
                    userRepository.save(user);
                    if (saveUser.getLang().equals(BotConstant.RU_SELECTED)) {
                        sendMessageToClient(chatId, "СОХРАНЕННЫЕ ДАННЫЕ:\n\n" +
                                "1️⃣ ФИО: " + user.getName() + "\n" +
                                "2️⃣ ЭЛЕКТРОННАЯ ПОЧТА: " + user.getEmail() + "\n" +
                                "3️⃣ ЛОГИН: " + user.getUserName() + "\n\n" +
                                "Подробно: http://localhost:3000/");
                    } else {
                        sendMessageToClient(chatId, "SAQLANGAN MA'LUMOTLAR:\n\n" +
                                "1️⃣ FISH: " + user.getName() + "\n" +
                                "2️⃣ EMAIL: " + user.getEmail() + "\n" +
                                "3️⃣ LOGIN: " + user.getUserName() + "\n\n" +
                                "Batafsil: http://localhost:3000/");
                    }
                }
            }

        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getFrom().getId().toString();
            String data = update.getCallbackQuery().getData();
            UserStep saveUser = saveUser(chatId);
            if (saveUser.getStep().equals(BotConstant.SELECT_LANG)) {
                if (data.equals(BotConstant.UZ_SELECTED)) {
                    sendMessageToClient(chatId, "O'zbek tili tanlandi. \nIltimos Ismingizni kiriting");
                    saveUser.setLang(BotConstant.UZ_SELECTED);
                    saveUser.setStep(BotConstant.ENTER_NAME);
                } else {
                    sendMessageToClient(chatId, "Русский язык выбран \nПожалуйста, введите Ваше имя");
                    saveUser.setLang(BotConstant.RU_SELECTED);
                    saveUser.setStep(BotConstant.ENTER_NAME);
                }
                System.out.println(saveUser.getStep());
            }
        }
    }

    private void sendMessageToClient(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private UserStep saveUser(String chatId) {
        for (UserStep user : userStep) {
            if (user.getChatId().equals(chatId)) {
                return user;
            }
        }
        UserStep user = new UserStep();
        user.setChatId(chatId);
        userStep.add(user);
        return user;
    }

}
