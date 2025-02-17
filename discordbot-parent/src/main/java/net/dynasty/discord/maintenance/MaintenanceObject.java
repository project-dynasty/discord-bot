package net.dynasty.discord.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dynasty.api.json.JsonConfig;
import net.dynasty.api.loader.LoadObject;
import net.dynasty.api.loader.config.ConfigLoader;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.backup.BackupEntry;
import net.dynasty.discord.backup.BackupNotFoundException;
import net.dynasty.discord.backup.ChannelPermissionEntry;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaintenanceObject extends ConfigLoader implements IMaintenanceObject {

    public MaintenanceObject() {
        super(new JsonConfig(new File("module/DiscordBot/maintenance.json")));
        load(new LoadInfo<>("maintenance", MaintenanceData.class, new MaintenanceData(false, -1, null, null, -1)));

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

        DiscordBot.INSTANCE.getBackupObject().saveBackup(entry -> {
            getDataOptional(MaintenanceData.class).get().setBackupId(entry.getId());
        });

        for (TextChannel textChannel : DiscordBot.INSTANCE.getGuild().getTextChannels()) {
            for (PermissionOverride permissionOverride : textChannel.getPermissionOverrides()) {
                permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
            }
        }
        for (VoiceChannel voiceChannel : DiscordBot.INSTANCE.getGuild().getVoiceChannels()) {
            for (PermissionOverride permissionOverride : voiceChannel.getPermissionOverrides()) {
                permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
            }
        }

        TextChannel channel = DiscordBot.INSTANCE.getGuild().createTextChannel("maintenance").complete();
        for (PermissionOverride permissionOverride : channel.getPermissionOverrides()) {
            permissionOverride.getManager().deny(Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE).queue();
        }
        channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Maintenance", "https://dynasty.net/warum_maintenance?").setDescription("**Reason**:\n" + getReason()).setFooter("Dynasty.net " + new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), DiscordBot.INSTANCE.getJda().getSelfUser().getAvatarUrl()).build()).queue();

        setMaintenanceChannel(channel);
        save("maintenance");

    }

    /*private void saveChannels() {
        List<PermissionOverride> oldTextChannelPermissionsArrayList = DiscordBot.INSTANCE.getGuild().getTextChannels().stream().flatMap(textChannel -> textChannel.getPermissionOverrides().stream()).collect(Collectors.toCollection(ArrayList::new));
        List<PermissionOverride> oldVoiceChannelPermissionsArrayList = DiscordBot.INSTANCE.getGuild().getVoiceChannels().stream().flatMap(voiceChannel -> voiceChannel.getPermissionOverrides().stream()).collect(Collectors.toCollection(ArrayList::new));

        List<String> textChannel = new ArrayList<>();
        List<String> voiceChannel = new ArrayList<>();
        for (PermissionOverride permissionOverride : oldTextChannelPermissionsArrayList) {
            textChannel.add(permissionOverride.getChannel().getName() + "~" + permissionOverride.getChannel().getId() + "~" + permissionOverride.getId() + "~" + permissionOverride.getAllowed() + "~" + permissionOverride.getDenied());
        }
        for (PermissionOverride permissionOverride : oldVoiceChannelPermissionsArrayList) {
            voiceChannel.add(permissionOverride.getChannel().getName() + "~" + permissionOverride.getChannel().getId() + "~" + permissionOverride.getId() + "~" + permissionOverride.getAllowed() + "~" + permissionOverride.getDenied());
        }

        getDataOptional(MaintenanceData.class).get().setTextChannel(textChannel);
        getDataOptional(MaintenanceData.class).get().setVoiceChannel(voiceChannel);

    }

    private void restoreChannel() {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;

        for (String s : getDataOptional(MaintenanceData.class).get().getTextChannel()) {
            String[] data = s.split("~");
            String name = data[0];

            long channelId = Long.parseLong(data[1]);
            long permissionId = Long.parseLong(data[2]);

            TextChannel channel = DiscordBot.INSTANCE.getGuild().getTextChannelById(channelId);

            if (channel == null) return;

            data[3] = data[3].replace("[", "");
            data[3] = data[3].replace("]", "");
            if (!data[3].equals("")) {
                List<Permission> allowed = Arrays.stream(data[3].split(", ")).map(Permission::valueOf).collect(Collectors.toList());
                channel.getPermissionOverrides().stream().filter(permissionOverride -> permissionOverride.getIdLong() == permissionId).findFirst().ifPresent(permissionOverride -> permissionOverride.getManager().grant(allowed).queue());
            }
        }
    }*/

    @Override
    public void disableMaintenance(boolean deleteBackup) {
        if (getDataOptional(MaintenanceData.class).isEmpty()) return;
        getDataOptional(MaintenanceData.class).get().setMaintenance(false);
        getDataOptional(MaintenanceData.class).get().setReason(null);

        getMaintenanceChannel().delete().queue();

        BackupEntry backupEntry = DiscordBot.INSTANCE.getBackupObject().getBackup(getDataOptional(MaintenanceData.class).get().getBackupId());
        if (backupEntry != null) {
            System.out.println("Load channels from backup " + backupEntry.getId());

            for (ChannelPermissionEntry permissionEntry : backupEntry.getAllEntries()) {
                GuildChannel guildChannel = DiscordBot.INSTANCE.getGuild().getChannels().stream().filter(channel -> channel.getIdLong() == permissionEntry.getChannelId()).findFirst().orElse(null);
                if (guildChannel == null) continue;

                PermissionOverride override = guildChannel.getPermissionOverrides().stream().filter(permissionOverride -> permissionOverride.getIdLong() == permissionEntry.getOverrideId()).findFirst().orElse(null);
                if (override == null) continue;

                override.getManager().setAllow(permissionEntry.getAllowed()).queue();
                override.getManager().setDeny(permissionEntry.getDenied()).queue();
            }
            if (deleteBackup)
                try {
                    DiscordBot.INSTANCE.getBackupObject().deleteBackup(backupEntry.getId());
                } catch (BackupNotFoundException e) {
                    e.printStackTrace();
                }
        }

        getDataOptional(MaintenanceData.class).get().setBackupId(null);
        save("maintenance");

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
        private String backupId;
        private long maintenanceChannel;
    }
}
