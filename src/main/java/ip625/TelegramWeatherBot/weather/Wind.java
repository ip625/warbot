package ip625.TelegramWeatherBot.weather;

//формат данных от OpenWeather
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("deg")
    @Expose
    private Double deg;

    public Double getSpeed() {
        return speed;
    }

    public Double getDeg() {
        return deg;
    }

}