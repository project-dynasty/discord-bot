package net.dynasty.discord.backup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public class ChannelPermissionEntry {

    private final String name;
    private final long id;
    private final long overrideId;
    private final EnumSet<Permission> allowed;
    private final EnumSet<Permission> denied;

}
