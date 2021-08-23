package net.dynasty.discord.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dynasty.discord.DiscordBot;
import net.verany.api.json.JsonConfig;
import net.verany.api.loader.LoadObject;
import net.verany.api.loader.config.ConfigLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MaintenanceObject extends ConfigLoader implements IMaintenanceObject {

    public MaintenanceObject() {
        super(new JsonConfig(new File("module/DiscordBot/maintenance.json")));
        load(new LoadInfo<>("maintenance", MaintenanceData.class, new MaintenanceData(false, -1, null)));

        if (shouldEnableMaintenance() && !isMaintenance())
            enableMaintenance(getReason());
    }

    @Override
    public boolean isMaintenance() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return true;
        return getDataOptional(MaintenanceData.class).get().isMaintenance();
    }

    @Override
    public void enableMaintenance(@Nullable String reason) {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        getDataOptional(MaintenanceData.class).get().setMaintenance(true);
        getDataOptional(MaintenanceData.class).get().setReason(reason);


    }

    @Override
    public void disableMaintenance() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        getDataOptional(MaintenanceData.class).get().setMaintenance(false);
    }

    @Override
    public void scheduleMaintenance(long timestampStart, @Nullable String reason) {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        getDataOptional(MaintenanceData.class).get().setScheduledMaintenance(timestampStart);
        getDataOptional(MaintenanceData.class).get().setReason(reason);
    }

    @Override
    public boolean isScheduledMaintenance() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return false;
        return getDataOptional(MaintenanceData.class).get().getScheduledMaintenance() > System.currentTimeMillis();
    }

    @Override
    public boolean shouldEnableMaintenance() {
        return getStartTimestamp() != -1 && getTimeLeft() < System.currentTimeMillis();
    }

    @Override
    public long getStartTimestamp() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return -1;
        return getDataOptional(MaintenanceData.class).get().getScheduledMaintenance();
    }

    @Override
    public long getTimeLeft() {
        return getStartTimestamp() - System.currentTimeMillis();
    }

    @Override
    public @Nullable String getReason() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return null;
        return getDataOptional(MaintenanceData.class).get().getReason();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MaintenanceData implements LoadObject {
        private boolean maintenance;
        private long scheduledMaintenance;
        private String reason;
    }
}
