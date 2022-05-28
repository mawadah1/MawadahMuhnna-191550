package com.example.database.openweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class OpenWeatherWrapper {

    /**
     * OpenWeather API key
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final String apiKey = "daa552293784b6f0641edf0abcc39790";
    /**
     * OpenWeather API url
     */
    private static final String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
    /**
     * OpenWeather icons url
     */
    private static final String iconsUrl = "https://openweathermap.org/img/wn";

    /**
     * OpenWeather query city
     */
    private static String city = "Berlin";

    /**
     * Sets query city
     */
    public static void setCity(String newCity) {
        city = newCity;
    }

    /**
     * Gets query city
     */
    public static String getCity() {
        return city;
    }

    /**
     * Weather data changed callback
     */
    public interface OnWeatherDataListener {
        void onWeatherData(String main, String description, Bitmap icon, Double temp, Double humidity);

        void onWeatherError(String message);
    }

    private final RequestQueue requestQueue;

    public OpenWeatherWrapper(Context context) {
        // get new request queue
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Gets weather from OpenWeather
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void getWeather(OnWeatherDataListener listener) {
        if (requestQueue == null) return;

        // request weather
        String weatherUrl = apiUrl + String.format("?q=%s&appid=" + apiKey, city);
        StringRequest weatherRequest = new StringRequest(Request.Method.GET, weatherUrl, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String main = weatherObject.getString("main");
                String description = weatherObject.getString("description");
                String icon = weatherObject.getString("icon");
                String iconUrl = iconsUrl + String.format("/%s@4x.png", icon);
                JSONObject mainObject = jsonResponse.getJSONObject("main");
                Double temp = mainObject.getDouble("temp") - 273.0;
                Double humidity = mainObject.getDouble("humidity");

                // request icon
                ImageRequest imageRequest = new ImageRequest(iconUrl,
                        iconResponse -> {
                            if (listener != null) {
                                listener.onWeatherData(main, description, iconResponse, temp, humidity);
                            }
                        }, 0, 0,
                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888,
                        error -> {
                            if (listener != null) {
                                listener.onWeatherError(error.getMessage());
                            }
                        });
                requestQueue.add(imageRequest);
            } catch (JSONException e) {
                if (listener != null) {
                    listener.onWeatherError(e.getMessage());
                }
            }
        }, error -> {
            if (listener != null) {
                listener.onWeatherError(error.getMessage());
            }
        });
        requestQueue.add(weatherRequest);
    }
}
