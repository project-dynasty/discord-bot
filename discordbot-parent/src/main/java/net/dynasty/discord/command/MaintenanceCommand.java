package net.dynasty.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
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

import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceCommand extends AbstractCommand {

    private final Map<IDiscordPlayer, String> reasonMap = new HashMap<>();

    public MaintenanceCommand(String name) {
        super(name);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT, PermissionGroupLoader.DEVELOPER);
        setDescription("Here you can set the maintenance mode");
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

        if (reasonMapping != null) {
            String reason = reasonMapping.getAsString();
            reasonMap.put(user, reason);
        }

        event.replyEmbeds(new EmbedBuilder().setDescription(MessageFormat.format("Are you sure {0} the maintenance?", name)).setColor(Color.cyan).build()).addActionRow(Button.success(buttonName(name), "Confirm"), Button.secondary(buttonName("cancel"), "Cancel")).setEphemeral(true).queue();

    }

    @Override
    public void onButtonClick(IDiscordPlayer discordPlayer, ButtonClickEvent clickEvent, String name) {
        switch (name) {
            case "enable" -> {
                DiscordBot.INSTANCE.getMaintenanceObject().enableMaintenance(reasonMap.get(discordPlayer));
                clickEvent.replyEmbeds(new EmbedBuilder().setDescription("Enabled maintenance").setColor(Color.green).build()).setEphemeral(true).queue();
            }
            case "disable" -> {
                DiscordBot.INSTANCE.getMaintenanceObject().disableMaintenance();
                clickEvent.replyEmbeds(new EmbedBuilder().setDescription("Disabled maintenance success").setColor(Color.green).build()).setEphemeral(true).queue();
            }
            case "cancel" -> clickEvent.deferReply().setEphemeral(true).queue();
        }
    }
}
