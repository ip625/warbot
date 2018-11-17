
package ip625.TelegramWeatherBot.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("list")
    @Expose
    private java.util.List<WeatherList> list = null;

    public java.util.List<WeatherList> getList() {
        return list;
    }


}
