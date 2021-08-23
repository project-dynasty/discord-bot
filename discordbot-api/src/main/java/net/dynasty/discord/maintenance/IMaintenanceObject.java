package net.dynasty.discord.maintenance;

import org.jetbrains.annotations.Nullable;

public interface IMaintenanceObject {

    boolean isMaintenance();

    void enableMaintenance(@Nullable String reason);

    void disableMaintenance();

    void scheduleMaintenance(long timestampStart, @Nullable String reason);

    boolean isScheduledMaintenance();

    boolean shouldEnableMaintenance();

    long getStartTimestamp();

    long getTimeLeft();

    @Nullable
    String getReason();

}
