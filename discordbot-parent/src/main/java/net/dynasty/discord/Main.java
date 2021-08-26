package net.dynasty.discord;

import net.dynasty.api.DynastyPlugin;

public class Main {

    private static DynastyPlugin bot;

    public static void main(String[] args) {
        bot = new DiscordBot();
        bot.enable();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> bot.disable()));
    }

}
