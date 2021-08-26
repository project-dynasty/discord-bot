package net.dynasty.api.json;

import com.google.gson.JsonParser;
import net.dynasty.api.Dynasty;
import net.dynasty.api.loader.config.AbstractJsonConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonConfig extends AbstractJsonConfig {

    public JsonConfig(File file) {
        super(file);
        reloadFromFile();
    }

    @Override
    public void saveConfig() {
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (final IOException e) {
                return;
            }
        }
        try (final FileWriter writer = new FileWriter(this.file)) {
            writer.write(Dynasty.GSON.toJson(this.object));
        } finally {
            return;
        }
    }

    @Override
    public void reloadFromFile() {
        if (!this.file.exists()) {
            return;
        }
        try (final FileReader reader = new FileReader(this.file)) {
            this.object = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (final IOException e) {
        } finally {
            return;
        }
    }

    @Override
    public <T> T get(String key, Class<T> tClass) {
        if (!this.object.has(key)) return null;
        return Dynasty.GSON.fromJson(this.object.get(key), tClass);
    }

    @Override
    public <T> void set(String key, T t) {
        this.object.add(key, Dynasty.GSON.toJsonTree(t));
    }

    @Override
    public void fromString(String string) {
        this.object = JsonParser.parseString(string).getAsJsonObject();
    }
}