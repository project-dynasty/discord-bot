package net.dynasty.discord.player;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dynasty.discord.permission.IPermissionObject;
import net.dynasty.discord.permission.PermissionObject;
import net.verany.api.module.VeranyProject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class DiscordPlayer implements IDiscordPlayer{

    private Long uniqueId;

    private final Member member;
    private final User user;

    private IPermissionObject permissionObject;

    private final Map<Long, Consumer<MessageReaction.ReactionEmote>> reactionConsumer = new HashMap<>();

    public DiscordPlayer(Member member) {
        this.member = member;
        this.user = member.getUser();
    }

    @Override
    public void load(Long aLong) {
        this.uniqueId = aLong;

        this.permissionObject = new PermissionObject(this);
        this.permissionObject.load(aLong);
    }

    @Override
    public void update() {
        this.permissionObject.update();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getNickname() {
        String nickname = member.getNickname();
        return nickname == null ? user.getName() : nickname;
    }

    @Override
    public void sendMessage(String... message) {
        user.openPrivateChannel().queue(privateChannel -> {
            for (String s : message)
                privateChannel.sendMessage(s).queue();
        });
    }

    @Override
    public void sendMessage(MessageEmbed... messages) {
        user.openPrivateChannel().queue(privateChannel -> {
            for (MessageEmbed s : messages)
                privateChannel.sendMessage(s).queue();
        });
    }

    @Override
    public void setNickname(String nickname) {
        member.modifyNickname(nickname).queue();
    }

    @Override
    public void onJoin() {

    }
}
