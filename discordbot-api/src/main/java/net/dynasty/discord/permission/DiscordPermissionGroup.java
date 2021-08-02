package net.dynasty.discord.permission;

import lombok.Getter;
import net.verany.api.group.AbstractPermissionGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DiscordPermissionGroup extends AbstractPermissionGroup {

    public static final List<DiscordPermissionGroup> VALUES = new ArrayList<>();

    private final long discordId;

    public DiscordPermissionGroup(String name, Color color, String scoreboardId, String prefix, int joinPower, long discordId) {
        super(name, color, scoreboardId, prefix, joinPower);
        this.discordId = discordId;
        VALUES.add(this);
    }

    public DiscordPermissionGroup(String name, int joinPower, long discordId) {
        this(name, null, "", "", joinPower, discordId);
    }

    public static DiscordPermissionGroup getGroup(String name) {
        return VALUES.stream().filter(discordPermissionGroup -> discordPermissionGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
