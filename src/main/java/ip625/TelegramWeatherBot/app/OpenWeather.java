package ip625.TelegramWeatherBot.app;
//Получение прогноза для локации
// Схема работы:
//если есть координаты в базе, переходим к запросу погоды
//если нет - пробуем узнать координаты через геокодирование
//если есть свежая погода для этих координат в кэше - запрос повторно не отправляем

//{"cod":"200","message":0.0019,"cnt":40,"list":[{"dt":1541203200,"main":{"temp":282.19,"temp_min":282.19,"temp_max":282.34,"pressure":1030.65,"sea_level":1033.23,"grnd_level":1030.65,"humidity":92,"temp_kf":-0.15},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":88},"wind":{"speed":4.26,"deg":192.503},"rain":{"3h":0.095},"sys":{"pod":"n"},"dt_txt":"2018-11-03 00:00:00"},{"dt":1541214000,"main":{"temp":282.19,"temp_min":282.19,"temp_max":282.292,"pressure":1030.23,"sea_level":1032.78,"grnd_level":1030.23,"humidity":91,"temp_kf":-0.1},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":76},"wind":{"speed":4.21,"deg":226},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-03 03:00:00"},{"dt":1541224800,"main":{"temp":281.22,"temp_min":281.22,"temp_max":281.274,"pressure":1029.97,"sea_level":1032.52,"grnd_level":1029.97,"humidity":94,"temp_kf":-0.05},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"clouds":{"all":88},"wind":{"speed":4.36,"deg":221.503},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-03 06:00:00"},{"dt":1541235600,"main":{"temp":281.72,"temp_min":281.72,"temp_max":281.72,"pressure":1029.85,"sea_level":1032.35,"grnd_level":1029.85,"humidity":94,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":68},"wind":{"speed":4.76,"deg":229.5},"rain":{"3h":0.065},"sys":{"pod":"d"},"dt_txt":"2018-11-03 09:00:00"},{"dt":1541246400,"main":{"temp":281.916,"temp_min":281.916,"temp_max":281.916,"pressure":1029.31,"sea_level":1031.78,"grnd_level":1029.31,"humidity":93,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":92},"wind":{"speed":5.01,"deg":242.501},"rain":{"3h":0.145},"sys":{"pod":"d"},"dt_txt":"2018-11-03 12:00:00"},{"dt":1541257200,"main":{"temp":281.482,"temp_min":281.482,"temp_max":281.482,"pressure":1028.88,"sea_level":1031.45,"grnd_level":1028.88,"humidity":91,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":92},"wind":{"speed":5.32,"deg":250},"rain":{"3h":0.18},"sys":{"pod":"n"},"dt_txt":"2018-11-03 15:00:00"},{"dt":1541268000,"main":{"temp":281.55,"temp_min":281.55,"temp_max":281.55,"pressure":1028.9,"sea_level":1031.5,"grnd_level":1028.9,"humidity":92,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":92},"wind":{"speed":5.74,"deg":259},"rain":{"3h":0.205},"sys":{"pod":"n"},"dt_txt":"2018-11-03 18:00:00"},{"dt":1541278800,"main":{"temp":281.234,"temp_min":281.234,"temp_max":281.234,"pressure":1029.98,"sea_level":1032.66,"grnd_level":1029.98,"humidity":93,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":48},"wind":{"speed":5.3,"deg":295.001},"rain":{"3h":0.26},"sys":{"pod":"n"},"dt_txt":"2018-11-03 21:00:00"},{"dt":1541289600,"main":{"temp":279.179,"temp_min":279.179,"temp_max":279.179,"pressure":1032.07,"sea_level":1034.64,"grnd_level":1032.07,"humidity":95,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":0},"wind":{"speed":4.7,"deg":310.003},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-04 00:00:00"},{"dt":1541300400,"main":{"temp":277.721,"temp_min":277.721,"temp_max":277.721,"pressure":1033.8,"sea_level":1036.48,"grnd_level":1033.8,"humidity":99,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"02n"}],"clouds":{"all":8},"wind":{"speed":4.13,"deg":306.504},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-04 03:00:00"},{"dt":1541311200,"main":{"temp":277.097,"temp_min":277.097,"temp_max":277.097,"pressure":1035.44,"sea_level":1038.09,"grnd_level":1035.44,"humidity":100,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":3.62,"deg":302.504},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-04 06:00:00"},{"dt":1541322000,"main":{"temp":278.99,"temp_min":278.99,"temp_max":278.99,"pressure":1037.14,"sea_level":1039.88,"grnd_level":1037.14,"humidity":97,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":0},"wind":{"speed":2.47,"deg":299.501},"rain":{"3h":0.0049999999999999},"sys":{"pod":"d"},"dt_txt":"2018-11-04 09:00:00"},{"dt":1541332800,"main":{"temp":279.985,"temp_min":279.985,"temp_max":279.985,"pressure":1038.28,"sea_level":1040.83,"grnd_level":1038.28,"humidity":92,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":2.61,"deg":274.001},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-04 12:00:00"},{"dt":1541343600,"main":{"temp":278.562,"temp_min":278.562,"temp_max":278.562,"pressure":1038.75,"sea_level":1041.42,"grnd_level":1038.75,"humidity":94,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"02n"}],"clouds":{"all":8},"wind":{"speed":2.72,"deg":253.501},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-04 15:00:00"},{"dt":1541354400,"main":{"temp":277.786,"temp_min":277.786,"temp_max":277.786,"pressure":1039.24,"sea_level":1041.82,"grnd_level":1039.24,"humidity":97,"temp_kf":0},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":44},"wind":{"speed":3.32,"deg":224.001},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-04 18:00:00"},{"dt":1541365200,"main":{"temp":277.969,"temp_min":277.969,"temp_max":277.969,"pressure":1039.48,"sea_level":1042.09,"grnd_level":1039.48,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":24},"wind":{"speed":3.65,"deg":219.006},"rain":{"3h":0.01},"sys":{"pod":"n"},"dt_txt":"2018-11-04 21:00:00"},{"dt":1541376000,"main":{"temp":277.72,"temp_min":277.72,"temp_max":277.72,"pressure":1039.81,"sea_level":1042.46,"grnd_level":1039.81,"humidity":100,"temp_kf":0},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02n"}],"clouds":{"all":24},"wind":{"speed":3.88,"deg":215.502},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-05 00:00:00"},{"dt":1541386800,"main":{"temp":278.057,"temp_min":278.057,"temp_max":278.057,"pressure":1039.67,"sea_level":1042.24,"grnd_level":1039.67,"humidity":98,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":64},"wind":{"speed":4.01,"deg":213.004},"rain":{"3h":0.02},"sys":{"pod":"n"},"dt_txt":"2018-11-05 03:00:00"},{"dt":1541397600,"main":{"temp":278.277,"temp_min":278.277,"temp_max":278.277,"pressure":1039.6,"sea_level":1042.26,"grnd_level":1039.6,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":64},"wind":{"speed":4.16,"deg":214.502},"rain":{"3h":0.04},"sys":{"pod":"d"},"dt_txt":"2018-11-05 06:00:00"},{"dt":1541408400,"main":{"temp":278.866,"temp_min":278.866,"temp_max":278.866,"pressure":1039.67,"sea_level":1042.25,"grnd_level":1039.67,"humidity":98,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":92},"wind":{"speed":4.51,"deg":217.001},"rain":{"3h":0.01},"sys":{"pod":"d"},"dt_txt":"2018-11-05 09:00:00"},{"dt":1541419200,"main":{"temp":278.647,"temp_min":278.647,"temp_max":278.647,"pressure":1039.23,"sea_level":1041.74,"grnd_level":1039.23,"humidity":95,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":56},"wind":{"speed":4.46,"deg":220.505},"rain":{"3h":0.01},"sys":{"pod":"d"},"dt_txt":"2018-11-05 12:00:00"},{"dt":1541430000,"main":{"temp":277.729,"temp_min":277.729,"temp_max":277.729,"pressure":1038.7,"sea_level":1041.38,"grnd_level":1038.7,"humidity":96,"temp_kf":0},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02n"}],"clouds":{"all":12},"wind":{"speed":3.95,"deg":221.505},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-05 15:00:00"},{"dt":1541440800,"main":{"temp":277.212,"temp_min":277.212,"temp_max":277.212,"pressure":1037.84,"sea_level":1040.43,"grnd_level":1037.84,"humidity":99,"temp_kf":0},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":48},"wind":{"speed":3.81,"deg":214.002},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-05 18:00:00"},{"dt":1541451600,"main":{"temp":277.747,"temp_min":277.747,"temp_max":277.747,"pressure":1037.72,"sea_level":1040.37,"grnd_level":1037.72,"humidity":98,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":76},"wind":{"speed":3.31,"deg":219.5},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-05 21:00:00"},{"dt":1541462400,"main":{"temp":277.449,"temp_min":277.449,"temp_max":277.449,"pressure":1037.58,"sea_level":1040.09,"grnd_level":1037.58,"humidity":100,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":0},"wind":{"speed":3.45,"deg":213.001},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-06 00:00:00"},{"dt":1541473200,"main":{"temp":276.349,"temp_min":276.349,"temp_max":276.349,"pressure":1037.1,"sea_level":1039.7,"grnd_level":1037.1,"humidity":100,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"02n"}],"clouds":{"all":8},"wind":{"speed":3.46,"deg":210.502},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-06 03:00:00"},{"dt":1541484000,"main":{"temp":276.067,"temp_min":276.067,"temp_max":276.067,"pressure":1037.04,"sea_level":1039.7,"grnd_level":1037.04,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":44},"wind":{"speed":3.41,"deg":208.505},"rain":{"3h":0.01},"sys":{"pod":"d"},"dt_txt":"2018-11-06 06:00:00"},{"dt":1541494800,"main":{"temp":278.058,"temp_min":278.058,"temp_max":278.058,"pressure":1037.18,"sea_level":1039.81,"grnd_level":1037.18,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":88},"wind":{"speed":2.81,"deg":210.504},"rain":{"3h":0.06},"sys":{"pod":"d"},"dt_txt":"2018-11-06 09:00:00"},{"dt":1541505600,"main":{"temp":279.489,"temp_min":279.489,"temp_max":279.489,"pressure":1036.97,"sea_level":1039.52,"grnd_level":1036.97,"humidity":97,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":88},"wind":{"speed":2.74,"deg":214.501},"rain":{"3h":0.08},"sys":{"pod":"d"},"dt_txt":"2018-11-06 12:00:00"},{"dt":1541516400,"main":{"temp":279.118,"temp_min":279.118,"temp_max":279.118,"pressure":1036.95,"sea_level":1039.54,"grnd_level":1036.95,"humidity":99,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":88},"wind":{"speed":2.76,"deg":213.004},"rain":{"3h":0.12},"sys":{"pod":"n"},"dt_txt":"2018-11-06 15:00:00"},{"dt":1541527200,"main":{"temp":278.475,"temp_min":278.475,"temp_max":278.475,"pressure":1036.93,"sea_level":1039.5,"grnd_level":1036.93,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":76},"wind":{"speed":2.81,"deg":215.503},"rain":{"3h":0.11},"sys":{"pod":"n"},"dt_txt":"2018-11-06 18:00:00"},{"dt":1541538000,"main":{"temp":278.065,"temp_min":278.065,"temp_max":278.065,"pressure":1036.95,"sea_level":1039.5,"grnd_level":1036.95,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":68},"wind":{"speed":2.52,"deg":217.502},"rain":{"3h":0.05},"sys":{"pod":"n"},"dt_txt":"2018-11-06 21:00:00"},{"dt":1541548800,"main":{"temp":277.979,"temp_min":277.979,"temp_max":277.979,"pressure":1036.69,"sea_level":1039.26,"grnd_level":1036.69,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":68},"wind":{"speed":2.12,"deg":202.501},"rain":{"3h":0.03},"sys":{"pod":"n"},"dt_txt":"2018-11-07 00:00:00"},{"dt":1541559600,"main":{"temp":277.001,"temp_min":277.001,"temp_max":277.001,"pressure":1035.95,"sea_level":1038.56,"grnd_level":1035.95,"humidity":100,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":80},"wind":{"speed":2.47,"deg":193.501},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-07 03:00:00"},{"dt":1541570400,"main":{"temp":276.007,"temp_min":276.007,"temp_max":276.007,"pressure":1035.78,"sea_level":1038.31,"grnd_level":1035.78,"humidity":100,"temp_kf":0},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02d"}],"clouds":{"all":24},"wind":{"speed":2.89,"deg":198},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-07 06:00:00"},{"dt":1541581200,"main":{"temp":276.969,"temp_min":276.969,"temp_max":276.969,"pressure":1035.72,"sea_level":1038.33,"grnd_level":1035.72,"humidity":100,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":56},"wind":{"speed":2.41,"deg":199.504},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-07 09:00:00"},{"dt":1541592000,"main":{"temp":277.951,"temp_min":277.951,"temp_max":277.951,"pressure":1035.14,"sea_level":1037.75,"grnd_level":1035.14,"humidity":100,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":2.52,"deg":200},"rain":{},"sys":{"pod":"d"},"dt_txt":"2018-11-07 12:00:00"},{"dt":1541602800,"main":{"temp":276.421,"temp_min":276.421,"temp_max":276.421,"pressure":1034.44,"sea_level":1037.09,"grnd_level":1034.44,"humidity":100,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"02n"}],"clouds":{"all":8},"wind":{"speed":3.5,"deg":199.003},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-07 15:00:00"},{"dt":1541613600,"main":{"temp":275.313,"temp_min":275.313,"temp_max":275.313,"pressure":1034.11,"sea_level":1036.76,"grnd_level":1034.11,"humidity":100,"temp_kf":0},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":32},"wind":{"speed":3.41,"deg":207.502},"rain":{},"sys":{"pod":"n"},"dt_txt":"2018-11-07 18:00:00"},{"dt":1541624400,"main":{"temp":275.304,"temp_min":275.304,"temp_max":275.304,"pressure":1034,"sea_level":1036.65,"grnd_level":1034,"humidity":100,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":68},"wind":{"speed":2.96,"deg":216.003},"rain":{"3h":0.04},"sys":{"pod":"n"},"dt_txt":"2018-11-07 21:00:00"}],"city":{"id":498817,"name":"Saint Petersburg","coord":{"lat":59.8944,"lon":30.2642},"country":"RU"}}
//{"coord":{"lon":30.26,"lat":59.89},"weather":[{"id":803,"main":"Clouds","description":"пасмурно","icon":"04d"}],"base":"stations","main":{"temp":10,"pressure":1018,"humidity":81,"temp_min":10,"temp_max":10},"visibility":10000,"wind":{"speed":6,"deg":240},"clouds":{"all":75},"dt":1541250000,"sys":{"type":1,"id":7267,"message":0.002,"country":"RU","sunrise":1541222800,"sunset":1541253442},"id":498817,"name":"Saint Petersburg","cod":200}

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.sql.SQLException;


class  OpenWeather {
    static final String owmApiKey = "4241f2d335e860019f58cd55dede6519";
    private String str;

    String getOpenWeather(String city, String lat, String lon,  long chat_id, boolean repeated)  {
        System.out.println("Обрабатываю: " + city);
        city = CheckCity(city);
        String query ="";
        String forecast;
        boolean geoCodingFailed = false;
        boolean weatherFromCache = false;

        //обычно готовых координат нет (если только метод не вызывает подписка)
        //ищем город в базе данных, запоминаем координаты
        if(lat == null || lon == null || lat.length()==0 && lon.length()==0) {
            String[] arr;
            try {
                SQLconnection.Conn();
                arr = SQLconnection.RecallCity(city);
                SQLconnection.CloseDB();
                lat = arr[0];
                lon = arr[1];

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        }

        //болванка для запроса OWM
        if (lat != null && lon != null && lat.length() > 0 && lon.length() > 0)
                query = "lat=" + lat + "&lon=" + lon;

        else {
            //запускаем mapquest api
            System.out.println("Запускаю геокодинг");
            GeoCoding geoCoding = new GeoCoding();
            String[] geoInfo;

            try {
                geoInfo = geoCoding.getCoordinates(city);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "ошибка";
            }

            if (geoInfo == null) geoCodingFailed = true;
                else {
                    lat = geoInfo[0];
                    lon = geoInfo[1];
                    query = "lat=" + lat + "&lon=" + lon;
                    city = geoInfo[2];
                }
        }

        //если геокодинг не помог, можно подкинуть OWM локацию латиницей
        if (geoCodingFailed) {
                System.out.println("Геокодинг не справился");
                return "ошибка";
        }

        forecast = "http://api.openweathermap.org/data/2.5/forecast?" + query + "&units=metric&lang=ru&APPID=" + owmApiKey;

        //проверяем, нет ли в базе прогноза для этого города не старше 2 часов
        try {
            SQLconnection.Conn();
            str = SQLconnection.RecallWeather(city, lat, lon);
            if (str != null && str.length() > 0) {
                System.out.println("Погода из кэша:");
                weatherFromCache = true;
            }
            SQLconnection.CloseDB();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "ошибка";
        }

        //если нет - запрашиваем OWM
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
                    System.out.println("Ответ от OpenWeather получен:");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (str.length() < 5) return "ошибка";

        //System.out.println(str);

        //сохраняю в базу кто и про что последний раз спрашивал
        // (здесь, потому что, если информация не поступила,
        // то и в базу записывать нечего)
        try {
            SQLconnection.Conn();
            if (!repeated)
                SQLconnection.WriteDB(chat_id, city, lat, lon);

            //также сохраняю кэш погоды и город (если информация свежая)
            if (!weatherFromCache) SQLconnection.MemorizeWeather(city, str, lat, lon);
            SQLconnection.CloseDB();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return WeatherParsing.weatherStringParsing(str, city, lat, lon);
    }

    private String CheckCity(String message_text) {
        //ручные подстраховки для столиц
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