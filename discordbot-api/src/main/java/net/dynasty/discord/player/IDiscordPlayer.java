package net.dynasty.discord.player;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dynasty.api.interfaces.IDefault;
import net.dynasty.discord.permission.IPermissionObject;

public interface IDiscordPlayer extends IDefault<Long> {

    IPermissionObject getPermissionObject();

    String getName();

    String getNickname();

    User getUser();

    Member getMember();

    void sendMessage(String... messages);

    void sendMessage(MessageEmbed... messages);

    void setNickname(String nickname);

    void onJoin();

}
