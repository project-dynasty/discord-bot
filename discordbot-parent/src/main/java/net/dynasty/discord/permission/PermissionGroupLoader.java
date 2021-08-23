package net.dynasty.discord.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import net.dynasty.discord.DiscordBot;
import net.verany.api.json.AbstractJsonConfig;
import net.verany.api.json.JsonConfig;
import net.verany.api.loader.LoadObject;
import net.verany.api.loader.config.ConfigLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PermissionGroupLoader extends ConfigLoader implements IPermissionGroupLoader {

    public static final long MANAGEMENT = 0,
            DEVELOPER = 1,
            DEV_OPS_ENGINEER = 2,
            ARTIST = 3,
            MODERATOR = 4,
            CONCEPTION = 5;

    public PermissionGroupLoader() {
        super(new JsonConfig(new File("module/DiscordBot/groups.json")));
        load(new LoadInfo<>("groups", GroupLoadObject.class, new GroupLoadObject(new HashMap<>() {{
            put(MANAGEMENT, 870853143125188630L);
            put(DEVELOPER, 870852920592195635L);
            put(DEV_OPS_ENGINEER, 870853235471159336L);
            put(ARTIST, 870852998224547870L);
            put(MODERATOR, 870855661146226698L);
            put(CONCEPTION, 871236518692405308L);
        }})));
    }

    @Override
    public int getPlayersInGroup(long group) {
        Role role = DiscordBot.INSTANCE.getGuild().getRoleById(group);
        if (role == null) {
            long id = getDiscordId(group);
            if (id == -1) return -1;
            role = DiscordBot.INSTANCE.getGuild().getRoleById(id);
        }
        return DiscordBot.INSTANCE.getGuild().getMembersWithRoles(role).size();
    }

    @Override
    public long getDiscordId(long group) {
        if (getDataOptional(GroupLoadObject.class).isEmpty()) return -1;
        return getDataOptional(GroupLoadObject.class).get().getPermissionGroups().get(group);
    }

    @RequiredArgsConstructor
    @Getter
    public static class GroupLoadObject implements LoadObject {
        private final Map<Long, Long> permissionGroups;
    }
}
