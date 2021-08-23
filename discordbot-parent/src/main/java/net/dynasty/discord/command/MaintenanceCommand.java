package net.dynasty.discord.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;

public class MaintenanceCommand extends AbstractCommand {

    public MaintenanceCommand(String name) {
        super(name);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.DEVELOPER);
        setDescription("Here you can set the maintenance mode");
        setChannel(871677780117565440L);
        addOption(new OptionData(OptionType.BOOLEAN, "enabled", "enable maintenance", true),
                new OptionData(OptionType.STRING, "reason", "reasons for maintenance", true),
                new OptionData(OptionType.STRING, "begin", "begin of maintenance", false));
    }

    @Override
    public void onExecute(IDiscordPlayer user, SlashCommandEvent event) {
        OptionMapping enabled = event.getOption("enabled");
        if (enabled == null) {

            return;
        }
        boolean isEnabled = enabled.getAsBoolean();

        event.reply("Are you sure?").addActionRow(Button.success("confirm", "Confirm"), Button.secondary("cancel", "Cancel")).queue();

        /*if (args.length == 2) {
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
        }*/
    }
}
