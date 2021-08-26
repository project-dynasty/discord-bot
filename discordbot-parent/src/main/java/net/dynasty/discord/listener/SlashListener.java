package net.dynasty.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.api.Dynasty;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.command.AbstractCommand;
import net.dynasty.discord.command.CommandParser;
import net.dynasty.discord.command.handler.CommandManager;
import net.dynasty.discord.player.IDiscordPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class SlashListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        AbstractCommand command = CommandManager.getCommandByName(event.getName());
        if (command == null) return;
        if (command.getChannel() != -1 && command.getChannel() != event.getChannel().getIdLong()) {
            event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Dieser Command darf nur in " + DiscordBot.INSTANCE.getGuild().getTextChannelById(command.getChannel()).getAsMention() + " ausgeführt werden!").build()).setEphemeral(true).queue();
            return;
        }
        IDiscordPlayer user = Dynasty.getPlayer(String.valueOf(event.getUser().getIdLong()), IDiscordPlayer.class);
        if (!hasPermission(user, command)) {
            event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Ich konnte diesen Befehl nicht finden!").setFooter(user.getNickname(), user.getUser().getAvatarUrl()).build()).setEphemeral(true).queue();
            return;
        }
        command.onExecute(user, event);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getComponentId().split("_").length < 2) return;
        AbstractCommand command = CommandManager.getCommandByButtonId(event.getComponentId().split("_")[0]);
        if (command == null) return;
        IDiscordPlayer user = Dynasty.getPlayer(String.valueOf(event.getUser().getIdLong()), IDiscordPlayer.class);
        if (!hasPermission(user, command)) {
            return;
        }
        command.onButtonClick(user, event, event.getComponentId().split("_")[1]);
    }

    private boolean hasPermission(IDiscordPlayer user, AbstractCommand command) {
        List<Long> commandGroups = command.getPermissionGroups();
        boolean hasGroup = false;
        for (Long commandGroup : commandGroups)
            if (user.getPermissionObject().hasDiscordRank(commandGroup))
                hasGroup = true;
        return commandGroups.size() == 0 || hasGroup;
    }
}
