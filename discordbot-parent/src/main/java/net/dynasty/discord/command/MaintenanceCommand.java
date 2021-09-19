package net.dynasty.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;

import java.awt.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MaintenanceCommand extends AbstractCommand {

    private final Map<IDiscordPlayer, String> reasonMap = new HashMap<>();

    public MaintenanceCommand(String name) {
        super(name, "Here you can set the maintenance mode");
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.DEVELOPER);
        setChannel(871677780117565440L);
        addOption(new OptionData(OptionType.BOOLEAN, "enabled", "enable maintenance", true),
                new OptionData(OptionType.STRING, "reason", "reasons for maintenance", false),
                new OptionData(OptionType.STRING, "begin", "begin of maintenance", false));
    }

    @Override
    public void onExecute(IDiscordPlayer user, SlashCommandEvent event) {
        OptionMapping enabledMapping = event.getOption("enabled");
        OptionMapping reasonMapping = event.getOption("reason");
        boolean isEnabled = enabledMapping.getAsBoolean();
        String name = isEnabled ? "enable" : "disable";

        if (isEnabled && DiscordBot.INSTANCE.getMaintenanceObject().isMaintenance()) {
            event.replyEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription("Maintenance is already enabled!").setColor(Color.red).build()).addActionRow(Button.danger(buttonName("disable"), "Disable")).setEphemeral(true).queue();
            return;
        }

        if (!isEnabled && !DiscordBot.INSTANCE.getMaintenanceObject().isMaintenance()) {
            event.replyEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription("Maintenance is already disabled!").setColor(Color.red).build()).addActionRow(Button.danger(buttonName("enable"), "Enable")).setEphemeral(true).queue();
            return;
        }

        if (reasonMapping != null) {
            String reason = reasonMapping.getAsString();
            reasonMap.put(user, reason);
        }

        event.replyEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription(MessageFormat.format("Are you sure {0} the maintenance?", name)).setColor(Color.cyan).build()).addActionRow(Button.success(buttonName(name), "Confirm"), Button.secondary(buttonName("cancel"), "Cancel")).setEphemeral(true).queue();

    }

    @Override
    public void onButtonClick(IDiscordPlayer discordPlayer, ButtonClickEvent clickEvent, String name) {
        switch (name) {
            case "enable": {
                DiscordBot.INSTANCE.getMaintenanceObject().enableMaintenance(reasonMap.getOrDefault(discordPlayer, "unknown"));
                clickEvent.editMessageEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription("You enabled the maintenance").setColor(Color.green).build()).setActionRow(Button.danger(buttonName("disable"), "Disable")).queue();
                break;
            }
            case "disable": {
                clickEvent.editMessageEmbeds(new EmbedBuilder().setDescription("Do you want to delete the backup?").setColor(Color.cyan).build()).setActionRow(Button.danger(buttonName("delete"), "Yes"), Button.success(buttonName("keep"), "No"), Button.secondary(buttonName("cancel"), "Cancel")).queue();
                break;
            }
            case "delete": {
                DiscordBot.INSTANCE.getMaintenanceObject().disableMaintenance(true);
                clickEvent.editMessageEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription("You disabled the maintenance").setColor(Color.green).build()).setActionRow(new ArrayList<>()).queue();
                break;
            }
            case "keep": {
                DiscordBot.INSTANCE.getMaintenanceObject().disableMaintenance(false);
                clickEvent.editMessageEmbeds(new EmbedBuilder().setTitle("Maintenance").setDescription("You disabled the maintenance").setColor(Color.green).build()).setActionRow(new ArrayList<>()).queue();
                break;
            }
            case "cancel": {
                Collection<Button> actionRows = clickEvent.getMessage().getButtons().stream().map(button -> button = button.asDisabled()).collect(Collectors.toList());
                clickEvent.editMessageEmbeds(new EmbedBuilder().setColor(Color.DARK_GRAY).setTitle("Maintenance").setDescription("~~" + clickEvent.getMessage().getEmbeds().get(0).getDescription() + "~~").build()).setActionRow(actionRows).queue();
                break;
            }
        }
    }
}
