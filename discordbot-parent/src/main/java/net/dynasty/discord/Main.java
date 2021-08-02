package net.dynasty.discord;

import net.verany.api.module.VeranyProject;

public class Main {

    private static VeranyProject bot;

    public static void main(String[] args) {
        bot = new DiscordBot();
        bot.enable();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> bot.disable()));
    }

}
