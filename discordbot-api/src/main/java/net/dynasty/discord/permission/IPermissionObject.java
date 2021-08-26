package net.dynasty.discord.permission;

import net.dynasty.api.interfaces.IDefault;

public interface IPermissionObject extends IDefault<Long> {

    void setDiscordRank(long group);

    boolean hasDiscordRank(long group);

    //List<Long> getGroups();

    //boolean isTeamMember();

}
