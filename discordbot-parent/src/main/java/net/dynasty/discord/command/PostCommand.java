package net.dynasty.discord.command;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;
import net.dynasty.discord.post.Post;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostCommand extends AbstractCommand {

    private final List<Post> queuedPosts = new ArrayList<>();

    public PostCommand(String name) {
        super(name);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.MODERATOR, PermissionGroupLoader.CONCEPTION);
    }

    @SneakyThrows
    @Override
    public void onExecute(IDiscordPlayer user, MessageReceivedEvent event, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("queue")) {
                StringBuilder posts = new StringBuilder();
                for (Post queuedPost : queuedPosts) {
                    posts.append("``").append(queuedPost.getId()).append("``").append(" - ").append(new SimpleDateFormat("dd.MM. HH:mm:ss").format(queuedPost.getTimestamp())).append(" in ").append(queuedPost.getTextChannel().getAsMention()).append("\n");
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Currently queued posts:");
                embedBuilder.setDescription(posts.toString());
                if (queuedPosts.isEmpty())
                    embedBuilder.setDescription("Nothing here..");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
                return;
            }
            File attachedFile = getAttachedFile();
            if (attachedFile != null) {
                if (attachedFile.getName().endsWith(".txt")) {
                    String message = IOUtils.toString(new FileInputStream(attachedFile), StandardCharsets.UTF_8);
                    try {
                        TextChannel channel = DiscordBot.INSTANCE.getGuild().getTextChannelById(Long.parseLong(args[0]));
                        if (channel == null) {
                            event.getChannel().sendMessage(new EmbedBuilder().setDescription(MessageFormat.format("Could not find a channel with id {0}.", args[0])).setColor(Color.red).build()).queue();
                            return;
                        }
                        Post post = new Post(user, message, channel);
                        queuedPosts.add(post);
                        event.getChannel().sendMessage(MessageFormat.format("Please confirm your post with ``-post confirm {0}``, or preview with ``-post preview {0}``", post.getId())).queue();
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage(new EmbedBuilder().setDescription(MessageFormat.format("{0} is not a real number!", args[0])).setColor(Color.red).build()).queue();
                        return;
                    }
                }
                return;
            }
            return;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("confirm")) {
                String id = args[1];
                Post post = queuedPosts.stream().filter(queued -> queued.getId().equals(id)).findFirst().orElse(null);
                if (post == null) return;
                post.send();
                event.getChannel().sendMessage(new EmbedBuilder().setDescription(user.getNickname() + " posted **" + post.getId() + "** in " + post.getTextChannel().getAsMention() + ".").setColor(Color.cyan).build()).queue();
                queuedPosts.remove(post);
                return;
            }
            if (args[0].equalsIgnoreCase("preview")) {
                String id = args[1];
                Post post = queuedPosts.stream().filter(queued -> queued.getId().equals(id)).findFirst().orElse(null);
                if (post == null) return;
                post.send(event.getTextChannel());
                return;
            }
        }
    }
}
