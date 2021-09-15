package net.dynasty.discord.permission;

import java.util.Map;

public interface IPermissionGroupLoader {

    int getPlayersInGroup(long group);

    long getDiscordId(long group);

    long getSelfId(long group);

    Map<Long, Long> getGroups();

}
