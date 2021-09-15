package net.dynasty.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dynasty.api.Dynasty;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.command.MaintenanceCommand;
import net.dynasty.discord.command.PostCommand;
import net.dynasty.discord.command.handler.CommandManager;
import net.dynasty.discord.player.DiscordPlayer;
import net.dynasty.discord.player.IDiscordPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Guild guild = DiscordBot.INSTANCE.getJda().getGuildById(870339990335406090L);
        DiscordBot.INSTANCE.setGuild(guild);

        System.out.println("Loading member...");
        long timestamp = System.currentTimeMillis();
        if (guild == null) return;

        CommandManager.loadCommands();

        guild.loadMembers(member -> {
            IDiscordPlayer discordPlayer = new DiscordPlayer(member);
            discordPlayer.load(member.getIdLong());
            Dynasty.setPlayer(IDiscordPlayer.class, discordPlayer);
        }).onSuccess(unused -> {
            int took = Math.toIntExact((System.currentTimeMillis() - timestamp) / 1000);
            System.out.println("Loading complete! (Took " + took + "s)");

        });
    }
}
