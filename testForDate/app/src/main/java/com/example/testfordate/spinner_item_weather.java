package com.example.testfordate;

public class spinner_item_weather {
    private String weather_name;
    private int weather_img;

    public spinner_item_weather(String Weather_name, int Weather_img) {
        weather_name = Weather_name;
        weather_img = Weather_img;
    }

    public String getWeatherName() {
        return weather_name;
    }

    public int getWeatherImage() {
        return weather_img;
    }
}
