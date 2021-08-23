package net.dynasty.discord.permission;

import net.verany.api.loader.config.ConfigLoader;

public interface IPermissionGroupLoader {

    int getPlayersInGroup(long group);

    long getDiscordId(long group);

}
