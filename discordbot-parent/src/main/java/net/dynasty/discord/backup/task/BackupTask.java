package net.dynasty.discord.backup.task;

import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.backup.BackupEntry;

import java.util.concurrent.TimeUnit;

public class BackupTask implements Runnable {

    public BackupTask() {
        Thread thread = new Thread(this, "Backup-Task");
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            long backupInterval = DiscordBot.INSTANCE.getBackupObject().getBackupInterval();
            if (backupInterval == -1) return;
            BackupEntry lastBackupEntry = DiscordBot.INSTANCE.getBackupObject().getLastBackup();
            long lastBackup = lastBackupEntry == null ? -1 : lastBackupEntry.getTimestamp();
            if (lastBackup + TimeUnit.MINUTES.toMillis(backupInterval) < System.currentTimeMillis()) {
                DiscordBot.INSTANCE.getBackupObject().saveBackup(entry -> {
                    System.out.println("created auto-backup");
                });
            }
            try {
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
