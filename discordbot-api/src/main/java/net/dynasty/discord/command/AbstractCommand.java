package net.dynasty.discord.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.Verany;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class AbstractCommand {

    private final String name;
    private String description;
    private long channel;
    private final List<OptionData> optionData = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final List<Long> permissionGroups = new ArrayList<>();
    private final String btnId = Verany.generate(10);

    public abstract void onExecute(IDiscordPlayer user, SlashCommandEvent event);

    public void onButtonClick(IDiscordPlayer user, ButtonClickEvent clickEvent, String name) {

    }

    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void setPermissionGroups(Long... permissionGroups) {
        this.permissionGroups.addAll(Arrays.asList(permissionGroups));
    }

    public void setPermissionGroups(List<Long> permissionGroups) {
        this.permissionGroups.addAll(permissionGroups);
    }

    public void addOption(OptionData... option) {
        optionData.addAll(List.of(option));
    }

    public String buttonName(String name) {
        return btnId + "_" + name;
    }

    /*private final String name;
    private final List<String> aliases = new ArrayList<>();
    private final List<String> helpList = new ArrayList<>();
    private final List<Long> permissionGroups = new ArrayList<>();
    private String description;
    //private File attachedFile = null;
    private long channel = -1;

    public abstract void onExecute(IDiscordPlayer user, SlashCommandEvent event, String[] args);

    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void setHelpList(String help) {
        this.helpList.add(help);
    }

    public void setPermissionGroups(Long... permissionGroups) {
        this.permissionGroups.addAll(Arrays.asList(permissionGroups));
    }

    public void setPermissionGroups(List<Long> permissionGroups) {
        this.permissionGroups.addAll(permissionGroups);
    }*/

}
