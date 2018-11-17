package ip625.TelegramWeatherBot.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SQLconnection {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException  {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:BotUsers.s3db");
        statmt = conn.createStatement();
    }

    // --------Создание таблиц--------
    public static void CreateDB() throws SQLException {
        //сформировать строки показалось компактнее и быстрее, чем через preparedStatement

        //requests = имеющиеся чаты, статус подписки
        statmt.execute("CREATE TABLE if not exists 'requests' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'chat_id' INT, 'city' text, 'lat' text, 'lon' text, 'subscription' INT);");
        //cities - лог городов - избавляет от необходимости повторно искать координаты

        //благодаря geocoding позволяет постепенно коллекционировать названия русских городов кириллицей
        statmt.execute("CREATE TABLE if not exists 'cities' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'city' text, 'lat' text, 'lon' text);");
        //лог погоды по городу с координатами, чтобы не донимать сервис слишком часто. Считаем, что
        //"срок годности" погоды - 2 часа
        statmt.execute("CREATE TABLE if not exists 'weather' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'city' text, 'description' text, 'lat' text, 'lon' text, 'expiration' INT);");

        statmt.execute("CREATE TABLE if not exists 'userIDs' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'chat_id' INT,  UNIQUE(chat_id));");

    }


        // --------Заполнение таблицы requests --------
    public static void WriteDB(long chat_id, String city, String lat, String lon) throws SQLException{

        //если раньше уже были запросы без подписки, затираем их:
        resSet = statmt.executeQuery("SELECT * FROM requests where chat_id="
                                                + chat_id + " AND subscription=0;");
        if (resSet.next()) {
            resSet.close();
            statmt.executeUpdate("UPDATE requests set city='" + city + "', lat='"+ lat +
                    "', lon='" + lon + "' where chat_id=" + chat_id + " AND subscription=0;");
            System.out.println("Обновлен чат " + chat_id + ". Текущая локация: " + city);
        //иначе заводим новую строку. (наверняка можно компактнее,  в одно обращение к базе но SQLite толком не владею)
        } else {
            resSet.close();
            statmt.execute("INSERT INTO requests ('chat_id', 'city', 'lat', 'lon','subscription') VALUES ("
                    + chat_id + ", '" + city +"', '" + lat + "', '" + lon +"', 0); ");
            System.out.println("Добавлен чат " + chat_id + " для локации " + city);
        }
    }

    //-----Заполнение userIDs ---------------
    public static void WriteID(long chat_id) throws SQLException {

        statmt.execute("INSERT INTO 'userIDs' ('chat_id') VALUES (" + chat_id + "); ");
        System.out.println("Сохранен новый ID " + chat_id);
    }
    // --------Добавление подписчика--------
    public static void EntrySubscribe (long chat_id) throws SQLException{

        statmt.executeUpdate("UPDATE requests set subscription = 1 where chat_id=" + chat_id + ";");
        System.out.println("Подписка для " + chat_id + " создана");
    }

    // --------Удаление подписчика--------
    public static void EntryUnsubscribe (long chat_id) throws SQLException{

        statmt.executeUpdate("DELETE FROM requests where chat_id=" + chat_id + " AND subscription=1;");
        System.out.println("Подписки для " + chat_id + " отменены");
    }

    // -------- Вывод списка подписчиков--------
    public static List<String[]> getSubscribers() throws SQLException{

        List<String[]> list = new ArrayList<String[]>();
        resSet = statmt.executeQuery("SELECT * FROM requests where subscription=1;");

        while(resSet.next())
        {
            String[] arr = {resSet.getString("chat_id"), resSet.getString("city"), resSet.getString("lat"), resSet.getString("lon")};
            list.add(arr);
        }

        return list;
    }

    // --------Вывод списка знакомых ID ----------------
    public static List<Long> getChatIDs() throws SQLException{

        List<Long> list = new ArrayList<Long>();
        resSet = statmt.executeQuery("SELECT * FROM userIDs;");
        System.out.println("ID чатов из базы считаны:");
        while(resSet.next()) {
            long chat_id = (long)resSet.getInt("chat_id");
            list.add(chat_id);
            System.out.print(chat_id + " ");
        }
        System.out.println();
        return list;
    }


    // --------Заполнение таблицы cities --------
            //добавляем новый город с координатами
    public static void MemorizeCity (String city, String lat, String lon) throws SQLException{

        ResultSet cityResSet = statmt.executeQuery("SELECT * FROM cities where city='" + city + "';");
        if (cityResSet.next() && cityResSet.getString("lat") != null && cityResSet.getString("lon").length() > 0)
            cityResSet.close();
        else {
            cityResSet.close();
            statmt.execute("INSERT INTO cities ('city', 'lat', 'lon') VALUES ('"
                    + city +"', '" + lat + "', '" + lon +"'); ");
            System.out.println("В базу добавлена локация " + city + " с координатами [" + lat + ", " + lon + "]");
        }
    }


    // --------Поиск города в таблице cities --------
        //возвращает координаты
    public static String[] RecallCity(String city) throws SQLException{

        String[] arr = new String[2];
        resSet = statmt.executeQuery("SELECT * FROM cities where city='"+ city +"';");

        while(resSet.next())
        {
            arr[0] = resSet.getString("lat");
            arr[1] = resSet.getString("lon");

        }
        if (arr[0] != null) System.out.println("Координаты локации " + city + " взяты из базы: " + Arrays.toString(arr));

        return arr;
    }

    // --------Заполнение таблицы weather --------
        //кэш погоды, чтобы не формировать повторные запросы в течение 2 часов
    public static void MemorizeWeather (String city, String description, String lat, String lon) throws SQLException{

        long time = System.currentTimeMillis() / 60000;
        statmt.executeUpdate("DELETE FROM weather where expiration<=" + time + ";");

        long expTime = System.currentTimeMillis() / 60000 + 120;
        statmt.execute("INSERT INTO weather ('city', 'description', 'lat', 'lon', 'expiration') VALUES ('" + city + "', '"
            + description +"', '" + lat + "', '" + lon + "', "+ expTime + "); ");
        System.out.println("В базу добавлена погода для координат [" + lat + ", "+ lon + "].");

    }


    // --------Загрузка погоды для города из кэша в таблице weather --------
    public static String RecallWeather(String city, String lat, String lon) throws SQLException{

        //удаляем устаревшие записи
        long time = System.currentTimeMillis() / 60000;
        statmt.executeUpdate("DELETE FROM weather where expiration<=" + time + ";");

        resSet = statmt.executeQuery("SELECT * FROM weather where lat='" + lat + "' AND lon='" + lon +"';");

        while (resSet.next()) {
            if (//resSet.getString("city").equals(city) ||
                    (resSet.getString("lat").equals(lat)
                    && resSet.getString("lon").equals(lon))
            ) {
               // System.out.println("Погода из кэша");
                return resSet.getString("description");
            }
        }
        //если не нашли:
        return "";
    }

    // --------Закрытие базы--------
    public static void CloseDB() throws  SQLException {
        conn.close();
        statmt.close();
        resSet.close();
    }

}
