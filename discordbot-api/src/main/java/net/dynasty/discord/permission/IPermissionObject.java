package net.dynasty.discord.permission;

import net.dynasty.api.interfaces.IDefault;

import java.util.List;

public interface IPermissionObject extends IDefault<Long> {

    void setDiscordRank(long group);

    boolean hasDiscordRank(long group);

    List<Long> getGroupsId();

    List<Long> getDiscordGroups();

    boolean isTeamMember();

}
