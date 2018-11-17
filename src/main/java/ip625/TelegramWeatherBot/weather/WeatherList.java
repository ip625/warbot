
package ip625.TelegramWeatherBot.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherList {

    @SerializedName("main")
    @Expose
    private WeatherMain main;

    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = null;

    @SerializedName("wind")
    @Expose
    private Wind wind;

    @SerializedName("dt_txt")
    @Expose
    private String dtTxt;



    public WeatherMain getMain() {
        return main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public String getDtTxt() {
        return dtTxt;
    }

}
