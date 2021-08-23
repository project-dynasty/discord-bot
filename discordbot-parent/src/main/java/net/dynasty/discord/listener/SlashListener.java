package net.dynasty.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.command.AbstractCommand;
import net.dynasty.discord.command.CommandParser;
import net.dynasty.discord.command.handler.CommandManager;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;
import org.jetbrains.annotations.NotNull;

public class SlashListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        System.out.println("received");
        AbstractCommand command = CommandManager.getCommands().getOrDefault(event.getName(), null);
        if (command == null) return;
        if (command.getChannel() != -1 && command.getChannel() != event.getChannel().getIdLong()) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Dieser Command darf nur in " + DiscordBot.INSTANCE.getGuild().getTextChannelById(command.getChannel()).getAsMention() + " ausgeführt werden!").build()).queue();
            return;
        }
        IDiscordPlayer user = Verany.getPlayer(String.valueOf(event.getUser().getIdLong()), IDiscordPlayer.class);
        command.onExecute(user, event);
        event.deferReply().queue();
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        
    }
}
