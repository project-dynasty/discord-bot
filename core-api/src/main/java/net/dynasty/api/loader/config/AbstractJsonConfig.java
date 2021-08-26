package net.dynasty.api.loader.config;

import com.google.gson.JsonObject;

import java.io.File;

public abstract class AbstractJsonConfig {

    public final File file;

    public JsonObject object = new JsonObject();

    public AbstractJsonConfig(File file) {
        this.file = file;
    }

    public abstract void saveConfig();

    public abstract void reloadFromFile();

    public abstract <T> T get(String key, Class<T> tClass);

    public abstract <T> void set(String key, T t);

    public String toString() {
        return object.toString();
    }

    public abstract void fromString(String string);
}
