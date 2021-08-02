package net.dynasty.discord;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dynasty.discord.logger.Logger;
import net.verany.api.Verany;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.stream.Collectors;

@VeranyModule(name = "DiscordBot", version = "2021.7.1", authors = {"Maximilian Wiegmann"})
public class DiscordBot extends VeranyProject {

    public static DiscordBot INSTANCE;

    private JDA jda;
    private Guild guild;

    public DiscordBot() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        new Logger();
        Verany.loadModule(this, this::init);
    }

    @SneakyThrows
    private void init() {
        String token = getResourceFileAsString();

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MEMBERS
        );

        jda = JDABuilder.createLight(token).
                setStatus(OnlineStatus.ONLINE).
                setActivity(Activity.playing("our game")).build();

        jda.awaitReady();
        System.out.println("Bot enabled...");
    }

    @Override
    public void disable() {

    }

    private String getResourceFileAsString() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("token.txt")) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
