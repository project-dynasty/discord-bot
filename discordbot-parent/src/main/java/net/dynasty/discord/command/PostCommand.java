package net.dynasty.discord.command;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.player.IDiscordPlayer;
import net.dynasty.discord.post.Post;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PostCommand extends AbstractCommand {

    private final List<Post> queuedPosts = new ArrayList<>();

    public PostCommand(String name) {
        super(name);
    }

    @SneakyThrows
    @Override
    public void onExecute(IDiscordPlayer user, MessageReceivedEvent event, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("queue")) {
                StringBuilder posts = new StringBuilder();
                for (Post queuedPost : queuedPosts) {
                    posts.append("-post confirm ").append(queuedPost.getId()).append("\n");
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Currently queued posts:");
                embedBuilder.setDescription(posts.toString());
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
                            event.getChannel().sendMessage(MessageFormat.format("Could not find a channel with id {0}.", args[0])).queue();
                            return;
                        }
                        Post post = new Post(message, channel);
                        queuedPosts.add(post);
                        channel.sendMessage(MessageFormat.format("Please confirm your post with ``-post confirm {0}``, or preview with ``-post preview {0}``", post.getId())).queue();
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage(MessageFormat.format("{0} is not a real number!", args[0])).queue();
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
                event.getChannel().sendMessage("You posted **" + post.getId() + "**.").queue();
                post.send();
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
