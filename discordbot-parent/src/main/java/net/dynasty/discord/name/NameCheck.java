package net.dynasty.discord.name;

import net.dynasty.api.Dynasty;
import net.dynasty.discord.player.IDiscordPlayer;

import java.util.Arrays;
import java.util.List;

public class NameCheck implements Runnable {

    public NameCheck() {
        Thread thread = new Thread(this, "Name Check Thread");
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            for (IDiscordPlayer player : Dynasty.getPlayers(IDiscordPlayer.class)) {
                if (player.getPermissionObject().isTeamMember()) continue;
                String playersName = player.getNickname();
                boolean isAllowed = isAllowedNickname(playersName);
                boolean isAllowedStart = isAllowedStartNickname(playersName);
                if (isAllowed && isAllowedStart && player.getNickname().equals("I love this server!")) {
                    player.setNickname(player.getUser().getName());
                    continue;
                }
                if (!isAllowed || !isAllowedStart && player.getNickname().equals("I love this server!"))
                    player.setNickname("I love this server!");
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAllowedNickname(String nickname) {
        String allowedCharacter = "abcdefghijklmnopqrstuvwxyzüöä$|=^°'.,_»×-x1234567890/() ";
        List<String> allowed = Arrays.asList(allowedCharacter.split(""));
        for (String s : nickname.split("")) {
            if (!allowed.contains(s.toLowerCase()))
                return false;
        }
        return true;
    }

    private boolean isAllowedStartNickname(String nickname) {
        String allowedStartCharacter = "abcdefghijklmnopqrstuvwxyzäö$ü1234567890- ";
        return Arrays.asList(allowedStartCharacter.split("")).contains(nickname.split("")[0].toLowerCase());
    }
}
