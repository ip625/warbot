package ip625.TelegramWeatherBot.app;
//Получение прогноза для локации
// Схема работы:
//если есть координаты в базе, переходим к запросу погоды
//если нет - пробуем узнать координаты через геокодирование
//если есть свежая погода для этих координат в кэше - запрос повторно не отправляем

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

class  OpenWeather {
    static final String owmApiKey = "4241f2d335e860019f58cd55dede6519";
    private String str;

    //getOpenWeather - основной метод обработки локации
    String getOpenWeather(String city, String lat, String lon,  long chat_id, boolean repeated) throws IOException {
        System.out.println("Обрабатываю: " + city);
        city = CheckCity(city);
        String query ="";
        String forecast;
        boolean geoCodingFailed = false;
        boolean weatherFromCache = false;

        //обычно готовых координат нет (если только не передана локация или если метод вызывает подписка)
        //поэтому ищем город в базе данных, запоминаем координаты
        if(lat == null || lon == null || lat.length()==0 && lon.length()==0) {
            String[] arr;
            try {
                SQLconnection.Conn();
                arr = SQLconnection.RecallCity(city);
                SQLconnection.CloseDB();
                lat = arr[0];
                lon = arr[1];

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //болванка для запроса OWM
        if (lat != null && lon != null && lat.length() > 0 && lon.length() > 0)
                query = "lat=" + lat + "&lon=" + lon;

        else {
        //запускаем mapquest api - чтобы получить координаты, они надежнее, чем прямой запрос по городу в OpenWeather
            System.out.println("Запускаю геокодинг");
            GeoCoding geoCoding = new GeoCoding();
            String[] geoInfo;

            try {
                geoInfo = geoCoding.getCoordinates(city);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new IOException();
            }

            if (geoInfo == null) geoCodingFailed = true;
                else {
                    lat = geoInfo[0];
                    lon = geoInfo[1];
                    query = "lat=" + lat + "&lon=" + lon;
                    city = geoInfo[2];
                }
        }

        //если геокодинг не помог, возвращаемся и выводим пользователю сообщение об ошибке
        if (geoCodingFailed) {
                System.out.println("Геокодинг не справился");
                throw new IOException();
        }

        forecast = "http://api.openweathermap.org/data/2.5/forecast?" + query + "&units=metric&lang=ru&APPID=" + owmApiKey;

        //проверяем, нет ли в базе прогноза для этого города (не старше 2 часов)
        try {
            SQLconnection.Conn();
            str = SQLconnection.RecallWeather(city, lat, lon);
            if (str != null && str.length() > 0) {
                System.out.println("Погода из кэша:");
                weatherFromCache = true;
            }
            SQLconnection.CloseDB();
        } catch (Exception e) {
            throw new IOException();
        }

        //если в базе прогноза нет - запрашиваем OWM
        if (str == null || str.length() == 0) {
            synchronized(owmApiKey ) {
                System.out.println("Запрашиваю OpenWeatherMap");

                URL url = null;

                try {
                    url = new URL(forecast);
                } catch (MalformedURLException e) {
                    System.out.println("Адрес не подошел");
                }

                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String inputLine;

                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {

                        response.append(inputLine);

                    }

                    in.close();

                    str = response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (str.length() < 5) throw new IOException();

        System.out.println("Ответ от OpenWeather получен");
        //System.out.println(str);

        //сохраняю в базу кто и про что последний раз спрашивал
        //следующим шагом можно будет подписаться на эту локацию

        try {
            SQLconnection.Conn();
            if (!repeated)
                SQLconnection.WriteDB(chat_id, city, lat, lon);

            //также сохраняю кэш погоды и город (если информация свежая, а не из базы)
            if (!weatherFromCache) SQLconnection.MemorizeWeather(city, str, lat, lon);
            SQLconnection.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return WeatherParsing.weatherStringParsing(str, city, lat, lon);
    }

    private String CheckCity(String message_text) {
        //ручные подстраховки для двух столиц
        if (message_text.toLowerCase().contains("питер")
                |   message_text.toLowerCase().contains("спб")
        ) {
            message_text = "Санкт-Петербург";
        }
        if (message_text.toLowerCase().contains("мск")){
            message_text = "Москва";
        }
        return message_text;
    }
}