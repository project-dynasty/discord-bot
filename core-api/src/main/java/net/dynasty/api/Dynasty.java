package net.dynasty.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dynasty.api.interfaces.IDefault;
import net.dynasty.api.loader.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Dynasty {

    public static final List<Loader> LOADERS = new ArrayList<>();
    public static final List<PlayerLoaderData<?>> PLAYER_LOADER_DATA = new ArrayList<>();

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void shutdown() {

    }

    public static String generateString(int length) {
        StringBuilder result = new StringBuilder();
        while (result.length() < length)
            result.append(getChar());
        return result.toString();
    }

    private static char getChar() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int s = getInt(alphabet.length());
        return alphabet.charAt(s - 1);
    }

    private static int getInt(int max) {
        return (int) Math.ceil(Math.random() * max);
    }

    public static <T extends IDefault<?>> void setPlayer(Class<T> tClass, T player) {
        PLAYER_LOADER_DATA.add(new PlayerLoaderData<>(player.getUniqueId().toString(), tClass, player));
    }

    public static <T extends IDefault<?>> T getPlayer(String key, Class<T> tClass) {
        PlayerLoaderData<T> loaderData = getLoadData(key, tClass);
        if (loaderData == null) return null;
        return loaderData.getPlayer();
    }

    public static <T extends IDefault<?>> List<T> getPlayers(Class<T> tClass) {
        return PLAYER_LOADER_DATA
                .stream()
                .filter(playerLoaderData ->
                        playerLoaderData.getTClass().equals(tClass))
                .map(playerLoaderData ->
                        (T) playerLoaderData.getPlayer())
                .collect(Collectors.toList());
    }

    public static <T extends IDefault<?>> void removePlayer(String key, Class<T> tClass) {
        PLAYER_LOADER_DATA.remove(getLoadData(key, tClass));
    }

    private static <T extends IDefault<?>> PlayerLoaderData<T> getLoadData(String key, Class<T> tClass) {
        Optional<PlayerLoaderData<?>> loadData = PLAYER_LOADER_DATA
                .stream()
                .filter(playerLoaderData ->
                        playerLoaderData.getTClass().equals(tClass) && playerLoaderData.getKey().equals(key))
                .findFirst();
        if (loadData.isEmpty()) return null;
        return (PlayerLoaderData<T>) loadData.get();
    }

    @AllArgsConstructor
    @Getter
    public static class PlayerLoaderData<T extends IDefault<?>> {
        private final String key;
        private final Class<T> tClass;
        private final T player;
    }

}
