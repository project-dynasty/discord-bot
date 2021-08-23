package net.dynasty.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;

public class MaintenanceCommand extends AbstractCommand {

    public MaintenanceCommand(String name) {
        super(name);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.DEVELOPER);
    }

    @Override
    public void onExecute(IDiscordPlayer user, MessageReceivedEvent event, String[] args) {
        if (args.length == 2) {
            switch (args[0]) {
                case "enable" -> {
                    if(DiscordBot.INSTANCE.getMaintenanceObject().isMaintenance()) {

                        return;
                    }
                    DiscordBot.INSTANCE.getMaintenanceObject().enableMaintenance(args[1]);
                }
                case "disable" -> {
                    if(!DiscordBot.INSTANCE.getMaintenanceObject().isMaintenance()) {

                        return;
                    }
                    DiscordBot.INSTANCE.getMaintenanceObject().disableMaintenance();
                }
            }
        }
    }
}
