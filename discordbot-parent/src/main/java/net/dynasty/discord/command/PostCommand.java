package net.dynasty.discord.command;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;
import net.dynasty.discord.post.Post;

import java.awt.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostCommand extends AbstractCommand {

    public static final List<Post> QUEUED_POSTS = new ArrayList<>();

    public PostCommand(String name) {
        super(name);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.MODERATOR, PermissionGroupLoader.CONCEPTION);
        setChannel(871677780117565440L);
        setDescription("Here you can create a message sent by the Bot");
        addOption(new OptionData(OptionType.CHANNEL, "channel", "The channel where the message will be sent", true),
                new OptionData(OptionType.STRING, "message", "The message (if image -> null)", true),
                new OptionData(OptionType.BOOLEAN, "embed", "Is the message embed", false),
                new OptionData(OptionType.STRING, "color", "The color of the embed message", false).
                        addChoice("green", "green").
                        addChoice("cyan", "cyan").
                        addChoice("red", "red").
                        addChoice("yellow", "yellow").
                        addChoice("orange", "orange").
                        addChoice("blue", "blue"),
                new OptionData(OptionType.STRING, "title", "The title of the message", false),
                new OptionData(OptionType.BOOLEAN, "footer", "Sending Footer Credits", false),
                new OptionData(OptionType.BOOLEAN, "image", "Post image at the beginning of the message", false));
    }

    @Override
    public void onButtonClick(IDiscordPlayer user, ButtonClickEvent clickEvent, String name) {
        String postId = clickEvent.getComponentId().split("_")[2];
        switch (name) {
            case "post": {
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresentOrElse(post -> {
                    post.send();
                    QUEUED_POSTS.remove(post);
                    clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.green).setDescription("You successfully posted in " + DiscordBot.INSTANCE.getGuild().getTextChannelById(post.getTextChannel().getIdLong()).getAsMention()).build()).setEphemeral(true).queue();
                }, () -> clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("This post is no longer available!").build()).setEphemeral(true).queue());
                break;
            }
            case "preview": {
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresentOrElse(post -> {
                    post.send(clickEvent.getTextChannel());
                    clickEvent.deferEdit().queue();
                }, () -> clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.RED).setDescription("This post is no longer available!").build()).setEphemeral(true).queue());
                break;
            }
            case "cancel": {
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresent(post -> {
                    QUEUED_POSTS.remove(post);
                    clickEvent.editMessageEmbeds(new EmbedBuilder().setColor(Color.DARK_GRAY).setTitle("You cancelled this post.").setDescription("**Message**:\n||" + post.getMessage() + "||")/*.setFooter("Cancelled by " + user.getNickname() + " at " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(System.currentTimeMillis()), user.getUser().getAvatarUrl())*/.build()).setActionRow(Button.success("post", "Post").asDisabled(), Button.primary("preview", "Preview").asDisabled(), Button.secondary("cancel", "Cancel").asDisabled()).queue();
                    clickEvent.deferEdit().queue();
                });
                break;
            }
        }
    }

    @SneakyThrows
    @Override
    public void onExecute(IDiscordPlayer user, SlashCommandEvent event) {
        OptionMapping channel = event.getOption("channel");
        OptionMapping message = event.getOption("message");
        boolean embed = event.getOption("embed") != null && event.getOption("embed").getAsBoolean();
        Color color = getColor(event.getOption("color") != null ? event.getOption("color").getAsString() : "cyan");
        String title = event.getOption("title") != null ? event.getOption("title").getAsString() : null;
        boolean footer = event.getOption("footer") == null || event.getOption("footer").getAsBoolean();
        boolean image = event.getOption("image") != null && event.getOption("image").getAsBoolean();

        MessageChannel textChannel = channel.getAsMessageChannel();
        String messageString = message.getAsString();

        Post post = new Post(user, messageString.equalsIgnoreCase("null") ? null : messageString, textChannel);
        post.setImage(image);

        if (embed) {
            EmbedBuilder builder = new EmbedBuilder().setDescription(messageString).setTitle(title).setColor(color);
            if (footer)
                builder.setFooter("Dynasty.net " + new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), DiscordBot.INSTANCE.getJda().getSelfUser().getAvatarUrl());
            post.setEmbedBuilder(builder);
        }

        QUEUED_POSTS.add(post);

        if (image) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please upload your image in this channel!").setColor(Color.cyan).build()).setEphemeral(true).queue();
            return;
        }
        event.replyEmbeds(new EmbedBuilder().setTitle("Please confirm your action").setDescription("**Message**:\n" + messageString).setColor(Color.cyan).build()).setEphemeral(true).addActionRow(Button.success(buttonName("post_" + post.getId()), "Post"), Button.primary(buttonName("preview_" + post.getId()), "Preview"), Button.secondary(buttonName("cancel_" + post.getId()), "Cancel")).queue();
    }

    public static Post isPostingImage(IDiscordPlayer discordPlayer) {
        return QUEUED_POSTS.stream().filter(post -> post.getSender().getName().equals(discordPlayer.getName()) && post.isImage()).findFirst().orElse(null);
    }

    private Color getColor(String name) {
        Color color;
        try {
            Field field = Color.class.getField(name);
            color = (Color) field.get(null);
        } catch (Exception e) {
            color = null; // Not defined
        }
        return color;
    }
}
