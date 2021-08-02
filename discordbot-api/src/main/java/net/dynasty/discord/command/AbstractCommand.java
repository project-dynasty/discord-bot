package net.dynasty.discord.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dynasty.discord.player.IDiscordPlayer;
import net.verany.api.group.AbstractPermissionGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class AbstractCommand {

    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private final List<String> helpList = new ArrayList<>();
    private final List<AbstractPermissionGroup> permissionGroups = new ArrayList<>();
    private File attachedFile = null;

    public abstract void onExecute(IDiscordPlayer user, MessageReceivedEvent event, String[] args);

    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void setHelpList(String help) {
        this.helpList.add(help);
    }

    public void setPermissionGroups(AbstractPermissionGroup... permissionGroups) {
        this.permissionGroups.addAll(Arrays.asList(permissionGroups));
    }

    public void setPermissionGroups(List<AbstractPermissionGroup> permissionGroups) {
        this.permissionGroups.addAll(permissionGroups);
    }

}
