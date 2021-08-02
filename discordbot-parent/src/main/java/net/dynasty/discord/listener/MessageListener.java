package net.dynasty.discord.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.discord.command.CommandParser;
import net.dynasty.discord.command.handler.CommandManager;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            CommandManager.handleCommand(CommandParser.parser(event.getMessage().getContentRaw(), event));
        }
    }

}
