package net.dynasty.discord.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import net.dynasty.discord.DiscordBot;
import net.dynasty.discord.player.IDiscordPlayer;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PermissionObject implements IPermissionObject {

    private Long uniqueId;
    private final IDiscordPlayer discordPlayer;

    @Override
    public void load(Long aLong) {
        this.uniqueId = aLong;
    }

    @Override
    public void update() {

    }

    @Override
    public void setDiscordRank(long group) {
        DiscordBot.INSTANCE.getGuild().addRoleToMember(discordPlayer.getMember(), getRole(group)).queue();
    }

    @Override
    public boolean hasDiscordRank(long group) {
        return discordPlayer.getMember().getRoles().stream().anyMatch(role -> role.getIdLong() == group);
    }

    /*@Override
    public List<Long> getGroups() {
        return DiscordPermissionGroup.VALUES.stream().filter(discordPermissionGroup -> hasDiscordRank(discordPermissionGroup.getDiscordId())).collect(Collectors.toList());
    }

    @Override
    public boolean isTeamMember() {
        boolean isMember = false;
        for (DiscordPermissionGroup group : getGroups()) {
            if (group.getJoinPower() >= 83) {
                isMember = true;
                break;
            }
        }
        return isMember;
    }*/

    private Role getRole(long id) {
        return DiscordBot.INSTANCE.getJda().getRoleById(id);
    }

}
