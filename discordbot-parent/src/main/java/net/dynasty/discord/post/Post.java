package net.dynasty.discord.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dynasty.discord.DiscordBot;
import net.verany.api.Verany;

@Getter
@Setter
@RequiredArgsConstructor
public class Post {

    private final String id = Verany.generate(5);
    private final String message;
    private final TextChannel textChannel;
    private EmbedBuilder embedBuilder = null;

    public void send() {
        send(textChannel);
    }

    public void send(TextChannel textChannel) {
        if(embedBuilder != null) {

            return;
        }
        textChannel.sendMessage(message).queue();
    }

}
