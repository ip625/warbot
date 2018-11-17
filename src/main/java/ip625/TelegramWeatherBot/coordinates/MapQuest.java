package ip625.TelegramWeatherBot.coordinates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapQuest {

    @SerializedName("lat")
    @Expose
    private float lat;

    @SerializedName("lon")
    @Expose
    private float lon;

    @SerializedName("display_name")
    @Expose
    private String displayName;


    public String getLatitude () { return String.format ("%.2f", lat);}

    public String getLongitude () { return String.format ("%.2f", lon);}

    public String getDisplayName() {
        int index = displayName.indexOf(',');
        index = displayName.indexOf(',', index + 1);
        index = displayName.indexOf(',', index + 1);
        if (index != -1)
            return displayName.substring(0, index);
        else return displayName;}

}
