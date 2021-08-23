package net.dynasty.discord.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.command.MaintenanceCommand;
import net.dynasty.discord.command.PostCommand;
import net.dynasty.discord.command.handler.CommandManager;
import net.dynasty.discord.player.DiscordPlayer;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Guild guild = DiscordBot.INSTANCE.getJda().getGuildById(870339990335406090L);
        DiscordBot.INSTANCE.setGuild(guild);

        System.out.println("Loading member...");
        long timestamp = System.currentTimeMillis();
        if (guild == null) {
            System.out.println("guild is null");
            return;
        }

        for (Command command : guild.retrieveCommands().complete()) {
            command.delete().queue();
        }
        guild.updateCommands().queue();
        CommandManager.addCommand(new MaintenanceCommand("maintenance"));

        guild.loadMembers(member -> {

            System.out.println(member.getEffectiveName());
            IDiscordPlayer discordPlayer = new DiscordPlayer(member);
            discordPlayer.load(member.getIdLong());
            Verany.setPlayer(IDiscordPlayer.class, discordPlayer);
        }).onSuccess(unused -> {
            int took = Math.toIntExact((System.currentTimeMillis() - timestamp) / 1000);
            System.out.println("Loading complete! (Took " + took + "s)");
        });
    }
}
