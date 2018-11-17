package ip625.TelegramWeatherBot.app;

//ВНИМАНИЕ: С ПК НАДО ЗАПУСКАТЬ С VPN!!!

//Инициализация бота и первичное обращение к базе данных
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App {
    public static void main(String[] args) {

        //Инициализация и регистрация бота
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        ClassProjectWeatherBot bot = new ClassProjectWeatherBot();

        try {
            botsApi.registerBot(bot);
            Timer.setTimer(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        System.out.println("Бот запущен.");

        //Обращаемся к базе данных и считываем id чатов из старых сессий
        try {
            SQLconnection.Conn();
            SQLconnection.CreateDB();
            bot.chatIDs.addAll(SQLconnection.getChatIDs());
            SQLconnection.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
