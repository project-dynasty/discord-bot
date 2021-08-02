package net.dynasty.discord.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;

public class CommandParser {

    public static CommandContainer parser(String raw, MessageReceivedEvent event) {
        String beheaded = raw.replaceFirst("-", "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0];
        ArrayList<String> split = new ArrayList<>();
        Collections.addAll(split, splitBeheaded);
        String[] strings = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(strings);

        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, strings, event);
    }

    @Getter
    @AllArgsConstructor
    public static class CommandContainer {
        private final String raw;
        private final String beheaded;
        private final String[] splitBeheaded;
        private final String invoke;
        private final String[] strings;
        private MessageReceivedEvent event;
    }

}
