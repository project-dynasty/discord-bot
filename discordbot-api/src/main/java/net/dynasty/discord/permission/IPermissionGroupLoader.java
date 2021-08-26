package net.dynasty.discord.permission;

public interface IPermissionGroupLoader {

    int getPlayersInGroup(long group);

    long getDiscordId(long group);

}
