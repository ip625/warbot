
package ip625.TelegramWeatherBot.weather;

//формат данных от OpenWeather
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("description")
    @Expose
    private String description;

    public String getDescription() {
        return description;
    }


}