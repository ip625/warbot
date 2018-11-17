//Собственно, бот. Обработка и отправка сообщений.
package ip625.TelegramWeatherBot.app;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

public class ClassProjectWeatherBot extends TelegramLongPollingBot {
    List<Long> chatIDs = new ArrayList<Long>(); //локальный список ID данной сессиии
                                            // (предыдущие ID подгружаются в него при инициализации бота)
    private ThreadGroup threadGroup = new ThreadGroup("DownloadThreads");

    //onUpdateReceived - обработка входящих сообщений
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            Thread thread = new Thread();
            final long chat_id = update.getMessage().getChatId();

            //проверяем, что нам не прислали локацию
            if (update.getMessage().hasLocation()) {
                Location location = update.getMessage().getLocation();
                final Float lat = location.getLatitude();
                final Float lon = location.getLongitude();

                thread = new Thread(threadGroup, new Runnable() {
                    @Override
                    public void run() {
                        prepareAndSendResponse(chat_id, "", lat, lon);
                    }
                });

            //обработка текстовых сообщений:
            } else if (update.getMessage().hasText()) {
                final String message_text = update.getMessage().getText().trim();
                System.out.println("\n\r" + message_text);

            //проверяем, что не было спец. команд
                if (message_text.equals("/subscribe"))
                        thread = new Thread(threadGroup, new Runnable() {
                            @Override
                            public void run() {
                                createSubscription(chat_id);
                            }
                        });
                else if (message_text.equals("/unsubscribe"))
                        thread = new Thread(threadGroup, new Runnable() {
                            @Override
                            public void run() {
                                deleteSubscription(chat_id);
                            }
                        });
            //в новых чатах высылаем первичное приветствие
                else if (!chatIDs.contains(chat_id) || message_text.equals("/start")) {
                        meetAndGreet (chat_id);
                }
            //стандартная обработка сообщения - считаем, что нам указали локацию, пытаемся добыть прогноз
                else {
                        thread = new Thread(threadGroup, new Runnable() {
                            @Override
                            public void run() {
                                prepareAndSendResponse(chat_id, message_text, 190,190);
                            }
                        });
                }
            }
            //запускаем выбранный поток - отдельно, чтобы не подвешивать очередь сообщений
            thread.start();
        }
    }

    //meetAndGreet - обрабатывает новый чат
    private void meetAndGreet(long chat_id) {
        sendMessage(chat_id, "Здравствуйте! У меня можно узнать погоду " +
                "и подписаться на прогнозы." +
                "Чтобы узнать погоду, пожалуйста, укажите населенный пункт." +
                "Для более точных результатов лучше указывать регион и страну. " +
                "\n\rТакже Вы можете отправить мне свое местоположение через стандартные опции Telegram.");

        chatIDs.add(chat_id);
        try {
            SQLconnection.Conn();
            SQLconnection.WriteID (chat_id);
            SQLconnection.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //createSubscription - добавляет подписку на последний обработанный город
    private void createSubscription(long chat_id) {
        while (threadGroup.activeCount() > 1)
            try {
                wait(1000); //возможно не нужно, т.к. SQlite имеет свою очередь запросов
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        try {
            SQLconnection.Conn();
            SQLconnection.EntrySubscribe(chat_id);
            SQLconnection.CloseDB();
            sendMessage(chat_id, "Вы успешно подписаны на рассылку.");
        } catch (Exception  e) {
            e.printStackTrace();
            sendMessage(chat_id, "К сожалению, с созданием подписки возникли проблемы.");
        }
    }

    //deleteSubscription - удаляет все подписки для данного чата
    private void deleteSubscription(long chat_id) {
        while (threadGroup.activeCount() > 1)
            try {
                wait(1000); //возможно не нужно, т.к. SQlite имеет свою очередь запросов
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                SQLconnection.Conn();
                SQLconnection.EntryUnsubscribe(chat_id);
                SQLconnection.CloseDB();
                sendMessage(chat_id, "Все ваши подписки успешно отменены.");
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(chat_id, "К сожалению, с отменой подписки(подписок) возникли проблемы.");
            }
    }

    //prepareAndSendResponse - ОСНОВНОЙ МЕТОД: запрашиваем прогноз и отправляем пользователю
    private void prepareAndSendResponse (long chat_id, String message_text, float lat, float lon){

        String defaultText = "Населенный пункт не распознан. " +
                "Пожалуйста, попробуйте записать локацию с указанием региона и страны" +
                ", или просто отправьте мне свое местоположение.";

        //запрос к сайту погоды выполняется в отдельном классе
         OpenWeather openWeather = new OpenWeather();
         String latitude  =  (lat == 190) ? "" : String.format ("%.2f", lat);
         String longitude = (lon == 190) ? "" : String.format ("%.2f", lon);

        String text = null;
        try {
            text = openWeather.getOpenWeather(message_text, latitude, longitude, chat_id, false);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chat_id, defaultText); //извинения
            return;
        }

        //если все прошло удачно - отправляем подготовленный ответ:
        sendMessage(chat_id, text);
        System.out.println("Отправлено пользователю: \"" + text + "\"");
        //и предлагаем подписаться, объясняя как в будущем отписаться.
        explainSubscription(chat_id, text);
    }

    //sendMessage - отправка сообщений пользователю
    private void sendMessage (long chat_id, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chat_id)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //explainSubscription - объясняет пользователю как подписаться и отписаться
    private void explainSubscription(long chat_id, String text) {
        int cityStart = text.indexOf("локации ") + 8;
        int cityEnd = text.indexOf('.', cityStart);
        String cityForSQL = text.substring(cityStart, cityEnd);
        sendMessage(chat_id, "Для подписки на ежедневные прогнозы отправьте /subscribe. " +
                "Рассылка выполняется около 15:00 по Москве. " +
                "Отмена рассылки по команде: /unsubscribe");
    }

    //технические детали от бота:
    @Override
    public String getBotUsername() {
        return "ClassProjectWeatherBot";
    }

    @Override
    public String getBotToken() {
        return "609052810:AAHTN3TWeF89ThiQykr0T_XodfD-jqlKju0";
    }



   // ================== Р А С С Ы Л К А ==================

    //updateSubscribers - запускаемое по расписанию задание по рассылке для подписчиков
    Runnable updateSubscribers = new Runnable() {
        @Override
        public void run() {
            List<String[]> list = new ArrayList<String[]>();
            //читаем список из базы
            try {
                SQLconnection.Conn();
                list = SQLconnection.getSubscribers();
                SQLconnection.CloseDB();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Iterator<String[]> iter = list.iterator();
            OpenWeather openWeather = new OpenWeather();

            //обрабатываем по одному
            while (iter.hasNext()) {
                String[] arr = iter.next();
                long chat_id = Long.parseLong(arr[0]);
                String text = null;
                try {
                    text = openWeather.getOpenWeather(arr[1], arr[2], arr[3], chat_id, true);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                sendMessage(chat_id, text);
                System.out.println("Отправлено по рассылке: \"" + text + "\"");
            }
        }
    };
}