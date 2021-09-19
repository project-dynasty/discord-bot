package net.dynasty.discord.maintenance;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

public interface IMaintenanceObject {

    boolean isMaintenance();

    void enableMaintenance(@Nullable String reason);

    void disableMaintenance(boolean deleteBackup);

    void scheduleMaintenance(long timestampStart, @Nullable String reason);

    boolean isScheduledMaintenance();

    boolean shouldEnableMaintenance();

    long getStartTimestamp();

    long getTimeLeft();

    TextChannel getMaintenanceChannel();

    void setMaintenanceChannel(TextChannel channel);

    @Nullable
    String getReason();

}
