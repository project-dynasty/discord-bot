package net.dynasty.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.api.Dynasty;
import net.dynasty.discord.command.PostCommand;
import net.dynasty.discord.player.IDiscordPlayer;
import net.dynasty.discord.post.Post;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        IDiscordPlayer user = Dynasty.getPlayer(String.valueOf(event.getMember().getIdLong()), IDiscordPlayer.class);

        Post imagePost = PostCommand.isPostingImage(user);
        if (imagePost != null) {
            event.getMessage().getAttachments().get(0).downloadToFile().whenComplete((file, throwable) -> {
                event.getMessage().delete().queue();
                imagePost.getImageFiles().add(file);
                imagePost.send();
                PostCommand.QUEUED_POSTS.remove(imagePost);
            });
        }
    }
}
