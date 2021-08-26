package net.dynasty.discord;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dynasty.api.Dynasty;
import net.dynasty.api.DynastyPlugin;
import net.dynasty.api.plugin.DynastyModule;
import net.dynasty.discord.listener.MessageListener;
import net.dynasty.discord.listener.ReadyListener;
import net.dynasty.discord.listener.SlashListener;
import net.dynasty.discord.logger.Logger;
import net.dynasty.discord.maintenance.IMaintenanceObject;
import net.dynasty.discord.maintenance.MaintenanceObject;
import net.dynasty.discord.permission.IPermissionGroupLoader;
import net.dynasty.discord.permission.PermissionGroupLoader;

import java.io.*;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Getter
@Setter
@DynastyModule(name = "DiscordBot", version = "2021.8.1", authors = {"Maximilian Wiegmann"})
public class DiscordBot extends DynastyPlugin {

    public static DiscordBot INSTANCE;

    private JDA jda;
    private Guild guild;

    private IPermissionGroupLoader groupLoader;
    private IMaintenanceObject maintenanceObject;

    public DiscordBot() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        new Logger();
        init();
    }

    @SneakyThrows
    public void init() {
        String token = getResourceFileAsString();

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_EMOJIS
        );

        jda = JDABuilder.createDefault(token).
                enableIntents(intents).
                setStatus(OnlineStatus.ONLINE).
                addEventListeners(
                        new SlashListener(),
                        new ReadyListener(),
                        new MessageListener()
                ).
                setRawEventsEnabled(true).
                setMemberCachePolicy(MemberCachePolicy.ALL).
                enableCache(CacheFlag.VOICE_STATE).
                setActivity(Activity.playing("Dynasty")).build();

        jda.setAutoReconnect(true);
        jda.awaitReady();

        groupLoader = new PermissionGroupLoader();
        maintenanceObject = new MaintenanceObject();

        System.out.println("Bot enabled...");
    }

    @Override
    public void disable() {
        Dynasty.shutdown();
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
