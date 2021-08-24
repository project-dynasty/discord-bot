package net.dynasty.discord.command.handler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.command.AbstractCommand;
import net.dynasty.discord.command.CommandParser;
import net.dynasty.discord.command.MaintenanceCommand;
import net.dynasty.discord.command.PostCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CommandManager {

    private static final HashMap<String, AbstractCommand> commands = new HashMap<>();

    public static void loadCommands() {
        CommandListUpdateAction commands = DiscordBot.INSTANCE.getGuild().updateCommands();
        commands.addCommands(addCommand(new MaintenanceCommand("maintenance")), addCommand(new PostCommand("post")));
        commands.queue();
    }

    public static CommandData addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
        if (command.getAliases() != null)
            for (String alias : command.getAliases())
                commands.put(alias, command);

        CommandData commandData = new CommandData(command.getName(), command.getDescription());
        commandData.addOptions(command.getOptionData());
        return commandData;
    }

    public static AbstractCommand getCommandByName(String name) {
        return commands.getOrDefault(name, null);
    }

    public static AbstractCommand getCommandByButtonId(String id) {
        return commands.values().stream().filter(abstractCommand -> abstractCommand.getBtnId().equals(id)).findFirst().orElse(null);
    }

}
