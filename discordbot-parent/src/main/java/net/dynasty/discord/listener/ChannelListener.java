package net.dynasty.discord.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.maintenance.IMaintenanceObject;
import org.jetbrains.annotations.NotNull;

public class ChannelListener extends ListenerAdapter {

    private final IMaintenanceObject maintenanceObject = DiscordBot.INSTANCE.getMaintenanceObject();

    @Override
    public void onTextChannelCreate(@NotNull TextChannelCreateEvent event) {
        if (maintenanceObject.isMaintenance()) {
            for (PermissionOverride permissionOverride : event.getChannel().getPermissionOverrides()) {
                permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
            }
        }
    }

    @Override
    public void onVoiceChannelCreate(@NotNull VoiceChannelCreateEvent event) {
        if (maintenanceObject.isMaintenance()) {
            for (PermissionOverride permissionOverride : event.getChannel().getPermissionOverrides()) {
                permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
            }
        }
    }

    @Override
    public void onCategoryCreate(@NotNull CategoryCreateEvent event) {
        if (maintenanceObject.isMaintenance()) {
            for (PermissionOverride permissionOverride : event.getCategory().getPermissionOverrides()) {
                permissionOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
            }
        }
    }
}
