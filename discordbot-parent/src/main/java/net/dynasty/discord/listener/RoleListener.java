package net.dynasty.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.api.Dynasty;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.player.IDiscordPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RoleListener extends ListenerAdapter {

    private final long textChannelId = 871471089484709958L;

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        TextChannel textChannel = event.getGuild().getTextChannelById(textChannelId);
        if (textChannel == null) return;
        if (event.getUser().isBot()) return;
        IDiscordPlayer user = Dynasty.getPlayer(String.valueOf(event.getUser().getIdLong()), IDiscordPlayer.class);
        if (user == null) return;
        for (Role role : event.getRoles()) {
            long id = role.getIdLong();
            boolean exist = DiscordBot.INSTANCE.getGroupLoader().getSelfId(id) != 0;
            if (!exist) continue;
            textChannel.sendMessageEmbeds(new EmbedBuilder().setDescription(user.getNickname() + " is now " + role.getName()).setColor(Color.green).build()).queue();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        TextChannel textChannel = event.getGuild().getTextChannelById(textChannelId);
    }
}
