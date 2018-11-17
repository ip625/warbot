package ip625.TelegramWeatherBot.app;
//геокодирование через MapQuest

import ip625.TelegramWeatherBot.coordinates.MapQuest;
import ip625.TelegramWeatherBot.coordinates.MapQuestGSONDecoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GeoCoding {

    //getCoordinates - добывает координаты по названию
    String[] getCoordinates (String msg) throws UnsupportedEncodingException {
        String[] arr = new String[3];
        String str;
        String geoRequest =
                "http://open.mapquestapi.com/nominatim/v1/search.php?key=GZJAueVp6W9O18ApK6uzSoWkbuXTGGjG" +
                        "&format=json&q=" + URLEncoder.encode(msg, "UTF-8");

        try{
            URL url = new URL(geoRequest);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));

            String inputLine;

            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);

            }

            in.close();

            str = response.toString();

            if (str.length() < 3) return null;

            System.out.println("Ответ MapQuest получен\r\n");

            //собственно, запрос
            MapQuestGSONDecoder decoder = new MapQuestGSONDecoder();
            MapQuest mapQuest = decoder.parseMapQuest(str);

            arr[0] = mapQuest.getLatitude();
            arr[1] = mapQuest.getLongitude();
            arr[2] = mapQuest.getDisplayName();

            try {
                SQLconnection.Conn();
                SQLconnection.CreateDB();
                if (arr[0]  != null && arr[1] != null) {

                    //Если локализованное название  - русское, то тоже сохраняем его в базу
                    // и используем для вывода пользователю:
                    if (!isCyrillic(arr[2])) {
                        arr[2] = msg;
                    } else
                        SQLconnection.MemorizeCity(arr[2], arr[0], arr[1]);

                    //иначе сохраняем только название из сообщения пользователя
                    SQLconnection.MemorizeCity(msg, arr[0], arr[1]);
                }
                SQLconnection.CloseDB();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(arr.toString());
            return arr;
        }
        catch (Exception e) {
            return null;
        }
    }

    //isCyrillic - содержит ли строка кирилицу?
    static boolean isCyrillic(String str) {
        Pattern p = Pattern.compile("[а-яёА-ЯЁ]+");
        Matcher m = p.matcher(str);
        return m.find();
    }
}
