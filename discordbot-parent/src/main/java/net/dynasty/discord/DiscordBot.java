package net.dynasty.discord;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dynasty.discord.command.AbstractCommand;
import net.dynasty.discord.command.handler.CommandManager;
import net.dynasty.discord.listener.MessageListener;
import net.dynasty.discord.listener.ReadyListener;
import net.dynasty.discord.logger.Logger;
import net.dynasty.discord.maintenance.IMaintenanceObject;
import net.dynasty.discord.maintenance.MaintenanceObject;
import net.dynasty.discord.permission.IPermissionGroupLoader;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;
import net.verany.api.group.AbstractPermissionGroup;
import net.verany.api.json.JsonConfig;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;

import java.awt.*;
import java.io.*;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@Setter
@VeranyModule(name = "DiscordBot", version = "2021.7.1", authors = {"Maximilian Wiegmann"})
public class DiscordBot extends VeranyProject {

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
        Verany.loadModule(this, this::init);
    }

    @SneakyThrows
    private void init() {
        String token = getResourceFileAsString();

        registerHandler();
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MEMBERS
        );

        jda = JDABuilder.createLight(token).
                setStatus(OnlineStatus.ONLINE).
                addEventListeners(
                        new MessageListener(),
                        new ReadyListener()
                ).
                setActivity(Activity.playing("Dynasty")).build();

        jda.awaitReady();

        groupLoader = new PermissionGroupLoader();
        maintenanceObject = new MaintenanceObject();

        System.out.println("Bot enabled...");
    }

    @Override
    public void disable() {
        Verany.shutdown();
    }

    private void registerHandler() {
        EmbedBuilder couldNotFindCommand = new EmbedBuilder().setColor(Color.red).setDescription("Ich konnte diesen Befehl nicht finden!");

        CommandManager.addCommandHandler(UUID.randomUUID(), container -> {
            if (container.getRaw().startsWith("-")) {
                IDiscordPlayer user = Verany.getPlayer(String.valueOf(container.getEvent().getAuthor().getIdLong()), IDiscordPlayer.class);
                if (CommandManager.getCommands().containsKey(container.getInvoke())) {
                    List<Long> commandGroups = CommandManager.getCommands().get(container.getInvoke()).getPermissionGroups();
                    boolean hasGroup = false;
                    for (Long commandGroup : commandGroups)
                        if (user.getPermissionObject().hasDiscordRank(commandGroup))
                            hasGroup = true;
                    if (commandGroups.size() != 0 && !hasGroup) {
                        container.getEvent().getChannel().sendMessageEmbeds(couldNotFindCommand.setFooter(user.getNickname(), user.getUser().getAvatarUrl()).build()).queue();
                        return;
                    }
                    AbstractCommand command = CommandManager.getCommands().get(container.getInvoke());
                    if (!container.getEvent().getMessage().getAttachments().isEmpty()) {
                        CompletableFuture<File> downloadToFile = container.getEvent().getMessage().getAttachments().get(0).downloadToFile();
                        downloadToFile.whenComplete((file, throwable) -> {
                            command.setAttachedFile(file);
                            command.onExecute(user, container.getEvent(), container.getStrings());
                        });
                        return;
                    }
                    command.onExecute(user, container.getEvent(), container.getStrings());
                } else {
                    container.getEvent().getChannel().sendMessageEmbeds(couldNotFindCommand.setFooter(user.getNickname(), user.getUser().getAvatarUrl()).build()).queue();
                }
            }
        });
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
