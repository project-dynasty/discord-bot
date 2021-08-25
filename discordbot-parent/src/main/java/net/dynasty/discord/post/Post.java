package net.dynasty.discord.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Post {

    private final String id = Verany.generate(5);
    private final long timestamp = System.currentTimeMillis();
    private final IDiscordPlayer sender;
    private final String message;
    private final MessageChannel textChannel;
    private boolean image;
    private List<File> imageFiles = new ArrayList<>();
    private EmbedBuilder embedBuilder = null;

    public void send() {
        send(textChannel);
    }

    public void send(MessageChannel textChannel) {
        if (image) {
            for (File imageFile : imageFiles) {
                textChannel.sendFile(imageFile).queue();
            }
            if (message == null) return;
        }
        if (embedBuilder != null) {
            textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        }
        textChannel.sendMessage(message).queue();
    }

}
