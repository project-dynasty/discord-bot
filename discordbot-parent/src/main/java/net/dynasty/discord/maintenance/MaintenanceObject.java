package net.dynasty.discord.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.entities.CategoryImpl;
import net.dynasty.discord.DiscordBot;
import net.verany.api.json.JsonConfig;
import net.verany.api.loader.LoadObject;
import net.verany.api.loader.config.ConfigLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaintenanceObject extends ConfigLoader implements IMaintenanceObject {

    public MaintenanceObject() {
        super(new JsonConfig(new File("module/DiscordBot/maintenance.json")));
        load(new LoadInfo<>("maintenance", MaintenanceData.class, new MaintenanceData(false, -1, null, -1)));

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

        for (TextChannel textChannel : DiscordBot.INSTANCE.getGuild().getTextChannels()) {
            for (PermissionOverride permissionOverride : textChannel.getPermissionOverrides()) {
                if (permissionOverride.getManager().getAllowedPermissions().contains(Permission.VIEW_CHANNEL)) {
                    permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
                }
            }
        }
        for (VoiceChannel voiceChannel : DiscordBot.INSTANCE.getGuild().getVoiceChannels()) {
            for (PermissionOverride permissionOverride : voiceChannel.getPermissionOverrides()) {
                if (permissionOverride.getManager().getAllowedPermissions().contains(Permission.VIEW_CHANNEL)) {
                    permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
                }
            }
        }

        TextChannel channel = DiscordBot.INSTANCE.getGuild().createTextChannel("maintenance").complete();
        for (PermissionOverride permissionOverride : channel.getPermissionOverrides()) {
            permissionOverride.getManager().deny(Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE).queue();
        }
        channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Maintenance", "https://dynasty.net/warum_maintenance?").setDescription(getReason()).setFooter("Dynasty.net " + new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), DiscordBot.INSTANCE.getJda().getSelfUser().getAvatarUrl()).build()).queue();

        setMaintenanceChannel(channel);

    }

    private void saveChannels() {
        List<String> permissionList = new ArrayList<>();
        for (TextChannel textChannel : DiscordBot.INSTANCE.getGuild().getTextChannels()) {

        }
    }

    @Override
    public void disableMaintenance() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        getDataOptional(MaintenanceData.class).get().setMaintenance(false);

        getMaintenanceChannel().delete().queue();

        for (TextChannel textChannel : DiscordBot.INSTANCE.getGuild().getTextChannels()) {
            for (PermissionOverride permissionOverride : textChannel.getPermissionOverrides()) {
                permissionOverride.getManager().grant(Permission.VIEW_CHANNEL).queue();
            }
        }
        for (VoiceChannel voiceChannel : DiscordBot.INSTANCE.getGuild().getVoiceChannels()) {
            for (PermissionOverride permissionOverride : voiceChannel.getPermissionOverrides()) {
                permissionOverride.getManager().grant(Permission.VIEW_CHANNEL).queue();
            }
        }
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
    public TextChannel getMaintenanceChannel() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return null;
        if (getDataOptional(MaintenanceData.class).get().getMaintenanceChannel() == -1) return null;
        return DiscordBot.INSTANCE.getGuild().getTextChannelById(getDataOptional(MaintenanceData.class).get().getMaintenanceChannel());
    }

    @Override
    public void setMaintenanceChannel(TextChannel channel) {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        if (channel == null) {
            getDataOptional(MaintenanceData.class).get().setMaintenanceChannel(-1);
            return;
        }
        getDataOptional(MaintenanceData.class).get().setMaintenanceChannel(channel.getIdLong());
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
        private long maintenanceChannel;
        private final List<String> textChannel = new ArrayList<>();
        private final List<String> voiceChannel = new ArrayList<>();
    }
}
