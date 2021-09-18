package net.dynasty.discord.backup;

import lombok.Getter;
import lombok.Setter;
import net.dynasty.api.loader.LoadObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface IBackupObject {

    void saveBackup(Consumer<BackupEntry> onFinish);

    void loadBackup(String id, Runnable onFinish) throws BackupNotFoundException;

    void deleteBackup(String id) throws BackupNotFoundException;

    boolean existBackup(String id);

    BackupEntry getBackup(String id);

    List<BackupEntry> getBackups();

    BackupEntry getLastBackup();

    BackupEntry getFirstBackup();

    long getBackupInterval();

    void setBackupInterval(long interval);

    @Getter
    @Setter
    class BackupLoadObject implements LoadObject {
        private final List<BackupEntry> entries = new ArrayList<>();
        private long backupInterval = -1;
    }

}
