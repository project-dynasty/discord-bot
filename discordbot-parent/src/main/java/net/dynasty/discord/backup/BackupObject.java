package net.dynasty.discord.backup;

import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dynasty.api.json.JsonConfig;
import net.dynasty.api.loader.config.AbstractJsonConfig;
import net.dynasty.api.loader.config.ConfigLoader;
import net.dynasty.discord.DiscordBot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BackupObject extends ConfigLoader implements IBackupObject {

    public BackupObject() {
        super(new JsonConfig(new File("module/DiscordBot/backup.json")));

        load(new LoadInfo<>("backups", BackupLoadObject.class, new BackupLoadObject()));
    }

    private void save() {
        save("backups");
    }

    @Override
    public void saveBackup(Consumer<BackupEntry> onFinish) {
        BackupEntry entry = new BackupEntry();

        List<PermissionOverride> oldTextChannelPermissionsArrayList = DiscordBot.INSTANCE.getGuild().getTextChannels().stream().flatMap(textChannel -> textChannel.getPermissionOverrides().stream()).collect(Collectors.toCollection(ArrayList::new));
        List<PermissionOverride> oldVoiceChannelPermissionsArrayList = DiscordBot.INSTANCE.getGuild().getVoiceChannels().stream().flatMap(voiceChannel -> voiceChannel.getPermissionOverrides().stream()).collect(Collectors.toCollection(ArrayList::new));
        List<ChannelPermissionEntry> textChannel = oldTextChannelPermissionsArrayList.stream().map(permissionOverride -> new ChannelPermissionEntry(permissionOverride.getChannel().getName(), permissionOverride.getChannel().getIdLong(), permissionOverride.getIdLong(), permissionOverride.getAllowed(), permissionOverride.getDenied())).collect(Collectors.toList());
        List<ChannelPermissionEntry> voiceChannel = oldVoiceChannelPermissionsArrayList.stream().map(permissionOverride -> new ChannelPermissionEntry(permissionOverride.getChannel().getName(), permissionOverride.getChannel().getIdLong(), permissionOverride.getIdLong(), permissionOverride.getAllowed(), permissionOverride.getDenied())).collect(Collectors.toList());

        entry.getVoiceChannel().addAll(voiceChannel);
        entry.getTextChannel().addAll(textChannel);

        getLoadObject().getEntries().add(entry);
        save();
        onFinish.accept(entry);
    }

    @Override
    public void loadBackup(String id, Runnable onFinish) {
        if (!existBackup(id)) return;
        BackupEntry entry = getBackup(id);

        onFinish.run();
    }

    @Override
    public void deleteBackup(String id) {
        if (!existBackup(id)) return;
        BackupEntry entry = getBackup(id);
        getBackups().remove(entry);
        save();
    }

    @Override
    public boolean existBackup(String id) {
        return getBackup(id) != null;
    }

    @Override
    public BackupEntry getBackup(String id) {
        if (getLoadObject() == null) return null;
        return getBackups().stream().filter(backupEntry -> backupEntry.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<BackupEntry> getBackups() {
        if (getLoadObject() == null) return new ArrayList<>();
        return getLoadObject().getEntries();
    }

    @Override
    public BackupEntry getLastBackup() {
        return getBackups().stream().findFirst().orElse(null);
    }

    @Override
    public BackupEntry getFirstBackup() {
        List<BackupEntry> reversedBackups = new ArrayList<>(getBackups());
        Collections.reverse(reversedBackups);
        return reversedBackups.stream().findFirst().orElse(null);
    }

    @Override
    public long getBackupInterval() {
        if (getLoadObject() == null) return -1;
        return getLoadObject().getBackupInterval();
    }

    @Override
    public void setBackupInterval(long interval) {
        getLoadObject().setBackupInterval(interval);
        save();
    }

    private BackupLoadObject getLoadObject() {
        return getDataOptional(BackupLoadObject.class).orElse(null);
    }
}
