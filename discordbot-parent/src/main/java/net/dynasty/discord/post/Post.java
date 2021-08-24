package net.dynasty.discord.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;

@Getter
@Setter
@RequiredArgsConstructor
public class Post {

    private final String id = Verany.generate(5);
    private final long timestamp = System.currentTimeMillis();
    private final IDiscordPlayer sender;
    private final String message;
    private final MessageChannel textChannel;
    private EmbedBuilder embedBuilder = null;

    public void send() {
        send(textChannel);
    }

    public void send(MessageChannel textChannel) {
        if (embedBuilder != null) {
            textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        }
        textChannel.sendMessage(message).queue();
    }

}
