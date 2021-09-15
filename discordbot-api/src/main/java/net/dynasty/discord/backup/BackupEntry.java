package net.dynasty.discord.backup;

import lombok.Getter;
import net.dynasty.api.Dynasty;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BackupEntry {

    private final String id = Dynasty.generateString(8);
    private final long timestamp = System.currentTimeMillis();
    private final List<ChannelPermissionEntry> textChannel = new ArrayList<>();
    private final List<ChannelPermissionEntry> voiceChannel = new ArrayList<>();

}
