package main.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapter.DurationAdapter;
import main.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonFactory {
    private static Gson gsonInstance;

    private GsonFactory() {}

    public static Gson getGson() {
        if (gsonInstance == null) {
            synchronized (GsonFactory.class) {
                if (gsonInstance == null) {
                    gsonInstance = createGsonInstance();
                }
            }
        }
        return gsonInstance;
    }

    private static Gson createGsonInstance() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
