package net.dynasty.discord.permission;

import net.verany.api.interfaces.IDefault;

import java.util.List;

public interface IPermissionObject extends IDefault<Long> {

    void setDiscordRank(long group);

    boolean hasDiscordRank(long group);

    List<DiscordPermissionGroup> getGroups();

    boolean isTeamMember();

}
