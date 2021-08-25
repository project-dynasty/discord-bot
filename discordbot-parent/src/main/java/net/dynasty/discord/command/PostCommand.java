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
                new OptionData(OptionType.BOOLEAN, "image", "Post image at the beginning of the message"));
        //addSubCommand(new SubcommandData("image", "Upload a image").addOption(OptionType.CHANNEL, "channel", "The channel where the message will be sent", true));
    }

    @Override
    public void onButtonClick(IDiscordPlayer user, ButtonClickEvent clickEvent, String name) {
        switch (name) {
            case "post": {
                String postId = clickEvent.getComponentId().split("_")[2];
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresentOrElse(post -> {
                    post.send();
                    QUEUED_POSTS.remove(post);
                    clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.green).setDescription("You successfully posted in " + DiscordBot.INSTANCE.getGuild().getTextChannelById(post.getTextChannel().getIdLong()).getAsMention()).build()).setEphemeral(true).queue();
                }, () -> clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("This post is no longer available!").build()).setEphemeral(true).queue());
                break;
            }
            case "preview": {
                String postId = clickEvent.getComponentId().split("_")[2];
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresentOrElse(post -> {
                    post.send(clickEvent.getTextChannel());
                    clickEvent.deferEdit().queue();
                }, () -> clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("This post is no longer available!").build()).setEphemeral(true).queue());
                break;
            }
            case "cancel": {
                String postId = clickEvent.getComponentId().split("_")[2];
                QUEUED_POSTS.stream().filter(post -> post.getId().equals(postId)).findFirst().ifPresent(post -> {
                    QUEUED_POSTS.remove(post);
                    clickEvent.deferEdit().queue();
                    clickEvent.getChannel().deleteMessageById(clickEvent.getMessageId()).queue();
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
        boolean emebed = event.getOption("embed") != null && event.getOption("embed").getAsBoolean();
        Color color = getColor(event.getOption("color") != null ? event.getOption("color").getAsString() : "cyan");
        String title = event.getOption("title") != null ? event.getOption("title").getAsString() : null;
        boolean footer = event.getOption("footer") == null || event.getOption("footer").getAsBoolean();
        boolean image = event.getOption("image") != null && event.getOption("image").getAsBoolean();

        MessageChannel textChannel = channel.getAsMessageChannel();
        String messageString = message.getAsString();

        Post post = new Post(user, messageString.equalsIgnoreCase("null") ? null : messageString, textChannel);
        post.setImage(image);

        if (emebed) {
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

        /*if (emebed) {
            textChannel.sendMessageEmbeds(new EmbedBuilder().setDescription(messageString).setColor(color).setFooter("Dynasty.net " + new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), DiscordBot.INSTANCE.getJda().getSelfUser().getAvatarUrl()).build()).queue();
        } else {
            textChannel.sendMessage(messageString).queue();
        }*/

        /*if (args.length == 1) {
            if (args[0].equalsIgnoreCase("queue")) {
                StringBuilder posts = new StringBuilder();
                for (Post queuedPost : QUEUED_POSTS) {
                    posts.append("``").append(queuedPost.getId()).append("``").append(" - ").append(new SimpleDateFormat("dd.MM. HH:mm:ss").format(queuedPost.getTimestamp())).append(" in ").append(queuedPost.getTextChannel().getAsMention()).append("\n");
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Currently queued posts:");
                embedBuilder.setDescription(posts.toString());
                if (QUEUED_POSTS.isEmpty())
                    embedBuilder.setDescription("Nothing here..");
                event.replyEmbeds(embedBuilder.build()).queue();
                return;
            }
            *//*File attachedFile = getAttachedFile();
            if (attachedFile != null) {
                if (attachedFile.getName().endsWith(".txt")) {
                    String message = IOUtils.toString(new FileInputStream(attachedFile), StandardCharsets.UTF_8);
                    try {
                        TextChannel channel = DiscordBot.INSTANCE.getGuild().getTextChannelById(Long.parseLong(args[0]));
                        if (channel == null) {
                            event.replyEmbeds(new EmbedBuilder().setDescription(MessageFormat.format("Could not find a channel with id {0}.", args[0])).setColor(Color.red).build()).queue();
                            return;
                        }
                        Post post = new Post(user, message, channel);
                        QUEUED_POSTS.add(post);
                        event.reply(MessageFormat.format("Please confirm your post with ``-post confirm {0}``, or preview with ``-post preview {0}``", post.getId())).queue();
                    } catch (NumberFormatException e) {
                        event.replyEmbeds(new EmbedBuilder().setDescription(MessageFormat.format("{0} is not a real number!", args[0])).setColor(Color.red).build()).queue();
                        return;
                    }
                }
                return;
            }*//*
            return;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("confirm")) {
                String id = args[1];
                Post post = QUEUED_POSTS.stream().filter(queued -> queued.getId().equals(id)).findFirst().orElse(null);
                if (post == null) return;
                post.send();
                event.replyEmbeds(new EmbedBuilder().setDescription(user.getNickname() + " posted **" + post.getId() + "** in " + post.getTextChannel().getAsMention() + ".").setColor(Color.cyan).build()).queue();
                QUEUED_POSTS.remove(post);
                return;
            }
            if (args[0].equalsIgnoreCase("preview")) {
                String id = args[1];
                Post post = QUEUED_POSTS.stream().filter(queued -> queued.getId().equals(id)).findFirst().orElse(null);
                if (post == null) return;
                post.send(event.getTextChannel());
                return;
            }
        }*/
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
