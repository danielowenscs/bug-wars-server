package net.crusadergames.bugwars.parser;

import java.util.HashMap;
import java.util.Map;

public class Commands {

    public static Map<String, Integer> getConditionalCommands() {
        Map<String, Integer> conditionals = new HashMap<>();
        conditionals.put("ifEnemy", 30);
        conditionals.put("ifAlly", 31);
        conditionals.put("ifFood", 32);
        conditionals.put("ifEmpty", 33);
        conditionals.put("ifWall", 34);
        conditionals.put("goto", 35);
        return conditionals;
    }

    public static Map<String, Integer> getActionCommands() {
        Map<String, Integer> actions = new HashMap<>();
        actions.put("noop", 0);
        actions.put("mov", 10);
        actions.put("rotr", 11);
        actions.put("rotl", 12);
        actions.put("att", 13);
        actions.put("eat", 14);
        return actions;
    }
}
