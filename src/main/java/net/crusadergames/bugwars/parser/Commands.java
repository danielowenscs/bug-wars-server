package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.Util.Constants;
import java.util.HashMap;
import java.util.Map;

public class Commands {

    public static Map<String, Integer> getConditionalCommands() {
        Map<String, Integer> conditionals = new HashMap<>();
        conditionals.put(Constants.CONDITIONALCOMMAND_IFENEMY, 30);
        conditionals.put(Constants.CONDITIONALCOMMAND_IFALLY, 31);
        conditionals.put(Constants.CONDITIONALCOMMAND_IFFOOD, 32);
        conditionals.put(Constants.CONDITIONALCOMMAND_IFEMPTY, 33);
        conditionals.put(Constants.CONDITIONALCOMMAND_IFWALL, 34);
        conditionals.put(Constants.CONDITIONALCOMMAND_GOTO, 35);
        return conditionals;
    }

    public static Map<String, Integer> getActionCommands() {
        Map<String, Integer> actions = new HashMap<>();
        actions.put(Constants.ACTIONCOMMAND_NOOP, 0);
        actions.put(Constants.ACTIONCOMMAND_MOV, 10);
        actions.put(Constants.ACTIONCOMMAND_ROTR, 11);
        actions.put(Constants.ACTIONCOMMAND_ROTL, 12);
        actions.put(Constants.ACTIONCOMMAND_ATT, 13);
        actions.put(Constants.ACTIONCOMMAND_EAT, 14);
        return actions;
    }
}
