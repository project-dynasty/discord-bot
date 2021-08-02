package net.dynasty.discord.command.handler;

import net.dynasty.discord.command.AbstractCommand;
import net.dynasty.discord.command.CommandParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CommandManager {

    private static final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static final Map<UUID, Consumer<CommandParser.CommandContainer>> handler = new ConcurrentHashMap<>();

    public static void addCommandHandler(UUID uuid, Consumer<CommandParser.CommandContainer> consumer) {
        handler.put(uuid, consumer);
    }

    public static void handleCommand(CommandParser.CommandContainer container) {
        handler.forEach((uuid, consumer) -> consumer.accept(container));
    }

    public static void addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
        if (command.getAliases() != null)
            for (String alias : command.getAliases())
                commands.put(alias, command);
    }

    public static HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    public static Map<UUID, Consumer<CommandParser.CommandContainer>> getHandler() {
        return handler;
    }
}
