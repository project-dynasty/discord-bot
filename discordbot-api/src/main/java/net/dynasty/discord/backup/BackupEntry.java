package net.dynasty.discord.backup;

import lombok.Getter;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dynasty.api.Dynasty;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BackupEntry {

    private final String id = Dynasty.generateString(8);
    private final long timestamp = System.currentTimeMillis();
    private final List<ChannelPermissionEntry> textChannel = new ArrayList<>();
    private final List<ChannelPermissionEntry> voiceChannel = new ArrayList<>();
    private final List<ChannelPermissionEntry> categories = new ArrayList<>();

    public List<ChannelPermissionEntry> getAllEntries() {
        List<ChannelPermissionEntry> toReturn = new ArrayList<>();
        toReturn.addAll(textChannel);
        toReturn.addAll(voiceChannel);
        toReturn.addAll(categories);
        return toReturn;
    }

}
