package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Parser {

    private List<Integer> bytecode = new ArrayList<>();
    private Map<String,Integer> labels = new HashMap<>();
    private final Map<String, Integer> actionCommands = Commands.getActionCommands();
    private final Map<String, Integer> conditionalCommands = Commands.getConditionalCommands();

    public boolean isLocation = false;

    public List<Integer> parseTokens(List<String> tokens) {
        Map<String, Integer> labels = getLabels(tokens);

        for (String token : tokens) {

            if(isLocation){
                processConditionalLocation(token);
            }else if (token.startsWith(":")) {
                // process label
                processLabel(token);
            } else if (isActionCommand(token)) {
                // process action command
                processActionCommand(token);
            } else if (isConditionalCommand(token)) {
                // process conditional command
                processConditionalCommand(token);
            } else {
                throw new SyntaxException("Command not found " + token);
            }
        }


        return bytecode;
    }

    private void processConditionalLocation(String token) {
        if(!labels.containsKey(token)){
            throw new SyntaxException("Label does not exist: " + token);
        }
        bytecode.add(labels.get(token));
        isLocation = false;
    }


    private void processActionCommand(String token) {
        bytecode.add(actionCommands.get(token));
    }

    private void processConditionalCommand(String token) {
        bytecode.add(conditionalCommands.get(token));
        isLocation = true;
    }

    private void processLabel(String token) {
        String label = token.substring(1);
        if(label.isBlank()){
            throw new SyntaxException("Label " + " " +  " has to have a name");
        }
        if (!isValidLabel(label)) {
            throw new SyntaxException("Labels have to be alphanumerical, and start with a letter");
        }
        if(labels.containsKey(label)){
            throw new SyntaxException("Cannot invoke the same label twice :" + label);
        }
        labels.put(label, bytecode.size());
    }

    public Map<String, Integer> getLabels(List<String> tokens) {
        Map<String, Integer> labels = new HashMap<>();
        int instructionPointer = 0;
        for (String token : tokens) {
            if (token.contains("#")) {
                int colonIndex = token.indexOf(":");

                if (colonIndex > 0) {
                    throw new SyntaxException("labels have to start at the beginning on the line");
                }
                String label = token.substring(1);
                if (!isValidLabel(label)) {
                    throw new SyntaxException("Labels have to be alphanumerical, and start with a letter");
                }
                labels.put(label, instructionPointer);
            } else if (isConditionalCommand(token)) {
                instructionPointer += 2;
            } else if (isActionCommand(token)) {
                instructionPointer++;
            }

        }

        return labels;
    }

    public static Boolean isValidLabel(String label) {
        return label.matches("[A-Z][A-Z0-9]*");
    }

    private boolean isConditionalCommand(String token) {
        return conditionalCommands.containsKey(token);
    }

    private boolean isActionCommand(String token) {
        return actionCommands.containsKey(token);
    }

}
