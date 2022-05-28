package com.example.database;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.database.openweather.OpenWeatherWrapper;

public class WeatherFragment extends Fragment {

    /**
     * Weather fragment click callback
     */
    public interface OnWeatherClickListener {
        void onClick();
    }

    /**
     * Listener for weather fragment click
     */
    private OnWeatherClickListener onWeatherClickListener = null;

    private OpenWeatherWrapper weatherWrapper;

    private TextView weatherCity;
    private TextView weatherMain;
    private TextView weatherDescription;
    private ImageView weatherIcon;
    private TextView weatherTemp;
    private TextView weatherHumidity;

    public WeatherFragment() {
        super(R.layout.fragment_weather);
    }

    /**
     * Sets weather fragment on click listener
     */
    public void setOnWeatherClickListener(OnWeatherClickListener listener) {
        onWeatherClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) return null;

        // get UI elements
        weatherWrapper = new OpenWeatherWrapper(view.getContext());
        weatherCity = view.findViewById(R.id.weatherCity);
        weatherMain = view.findViewById(R.id.weatherMain);
        weatherDescription = view.findViewById(R.id.weatherDescription);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        weatherTemp = view.findViewById(R.id.weatherTemp);
        weatherHumidity = view.findViewById(R.id.weatherHumidity);

        // listen for view click
        view.setOnClickListener(v -> {
            if (onWeatherClickListener != null) {
                onWeatherClickListener.onClick();
            }
        });

        // initialize weather
        weatherCity.setText(OpenWeatherWrapper.getCity());
        weatherMain.setText(R.string.weather);
        weatherDescription.setText(R.string.no_weather_data);
        weatherIcon.setImageResource(R.drawable.question_mark);
        weatherTemp.setText(getString(R.string.temp, 0.0));
        weatherHumidity.setText(getString(R.string.humidity, 0.0));

        // update weather
        updateWeather();
        return view;
    }

    public void updateWeather() {
        if (weatherWrapper == null) return;

        // get weather from OpenWeather
        weatherWrapper.getWeather(new OpenWeatherWrapper.OnWeatherDataListener() {
            @Override
            public void onWeatherData(String main, String description, Bitmap icon, Double temp, Double humidity) {
                // weather received
                weatherCity.setText(OpenWeatherWrapper.getCity());
                weatherMain.setText(main);
                weatherDescription.setText(description);
                weatherIcon.setImageBitmap(icon);
                weatherTemp.setText(getString(R.string.temp, temp));
                weatherHumidity.setText(getString(R.string.humidity, humidity));
            }

            @Override
            public void onWeatherError(String message) {
                weatherCity.setText(OpenWeatherWrapper.getCity());
                weatherMain.setText(R.string.weather);
                weatherDescription.setText(R.string.no_weather_data);
                weatherIcon.setImageResource(R.drawable.question_mark);
                weatherTemp.setText(getString(R.string.temp, 0.0));
                weatherHumidity.setText(getString(R.string.humidity, 0.0));
            }
        });
    }
}