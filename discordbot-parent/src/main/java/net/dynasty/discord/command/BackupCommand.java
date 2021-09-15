package net.dynasty.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.backup.BackupEntry;
import net.dynasty.discord.permission.PermissionGroupLoader;
import net.dynasty.discord.player.IDiscordPlayer;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class BackupCommand extends AbstractCommand {

    public BackupCommand(String name, String description) {
        super(name, description);
        setPermissionGroups(PermissionGroupLoader.MANAGEMENT);
        addOption(new OptionData(OptionType.STRING, "type", "Type", true).
                        addChoice("save", "save").
                        addChoice("load", "load").
                        addChoice("list", "list").
                        addChoice("delete", "delete").
                        addChoice("interval", "interval"),
                new OptionData(OptionType.STRING, "id", "id of backup", false),
                new OptionData(OptionType.INTEGER, "interval", "interval of auto-backup", false));
        setAliases("bu");
    }

    @Override
    public void onExecute(IDiscordPlayer user, SlashCommandEvent event) {
        String type = event.getOption("type").getAsString();

        switch (type.toLowerCase()) {
            case "save": {
                DiscordBot.INSTANCE.getBackupObject().saveBackup(entry -> {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Created backup ``" + entry.getId() + "``").setColor(Color.cyan).setFooter("Timestamp: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(entry.getTimestamp())).build()).setEphemeral(true).queue();
                });
                break;
            }
            case "load": {
                if (event.getOption("id") == null) {

                    return;
                }
                String id = event.getOption("id").getAsString();
                DiscordBot.INSTANCE.getBackupObject().loadBackup(id, () -> {
                    event.replyEmbeds(new EmbedBuilder().setColor(Color.green).setDescription("loaded backup " + id).build()).setEphemeral(true).addActionRow(Button.danger(buttonName("delete_" + id), "Delete Backup")).queue();
                });
                break;
            }
            case "list": {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("List of backups");
                for (BackupEntry backup : DiscordBot.INSTANCE.getBackupObject().getBackups()) {
                    embedBuilder.addField(backup.getId(), new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(backup.getTimestamp()), false);
                }
                event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
                break;
            }
            case "delete": {
                if (event.getOption("id") == null) {

                    return;
                }
                String id = event.getOption("id").getAsString();
                DiscordBot.INSTANCE.getBackupObject().deleteBackup(id);
                event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Deleted backup " + id).build()).setEphemeral(true).queue();
                break;
            }
            case "interval": {
                if (event.getOption("interval") == null) {

                    return;
                }
                long interval = event.getOption("interval").getAsLong();
                DiscordBot.INSTANCE.getBackupObject().setBackupInterval(interval);
                event.replyEmbeds(new EmbedBuilder().setDescription("Set backup interval to " + TimeUnit.MINUTES.toHours(interval) + "h " + interval + "m").setColor(Color.cyan).build()).setEphemeral(true).queue();
                break;
            }
        }
    }

    @Override
    public void onButtonClick(IDiscordPlayer user, ButtonClickEvent clickEvent, String name) {
        switch (name) {
            case "delete": {
                String id = clickEvent.getComponentId().split("_")[2];
                DiscordBot.INSTANCE.getBackupObject().deleteBackup(id);
                clickEvent.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Deleted backup " + id).build()).setEphemeral(true).queue();
                break;
            }
        }
    }
}
