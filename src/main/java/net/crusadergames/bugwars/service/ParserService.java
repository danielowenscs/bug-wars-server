package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParserService {

    public boolean validateScript(String scriptBody) {
        String script = "    :START  attack\n" +
                "     moveForward\n" +
                "\n" +
                "    GOTO DOBBY\n" +
                ":DOBBY  eat \n";

        final int MIN_EXPRESSIONS = 1;
        final int MAX_EXPRESSIONS = 2;

        List<String> lines = new ArrayList<String>(Arrays.asList(script.split("\\n")));
        Map<String, Boolean> declaredLabels = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) {
                continue;
            }

            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));
            }

            if (line.isEmpty()) {
                continue;
            }

            final String[] expressions = line.split("\\r+");
            final int expressionsLength = expressions.length;

            if (expressions.length < MIN_EXPRESSIONS) {
                continue;
            }

            if (expressions.length > MAX_EXPRESSIONS) {
                throw new SyntaxException("Too many arguments on line " + i);
            }


            int keywordIndex = 0;

            if (expressionsLength == MAX_EXPRESSIONS) {
                keywordIndex = 1;
            }

            boolean validLabel = false;
            if (expressions[0].matches("[:][A-Z][A-Z0-9]*")) {
                if (declaredLabels.containsKey(expressions[0])) {
                    if (declaredLabels.get(expressions[0])) {
                        throw new SyntaxException("Label at line " + " is duplicate");
                    }

                    throw new SyntaxException("Label at line " + i + " is duplicate");
                }
                declaredLabels.put(expressions[0], true);
                validLabel = true;
            }

            if (expressionsLength == MAX_EXPRESSIONS && !validLabel) {
                throw new SyntaxException("Invalid label on line " + i);
            }

            if (keywordIndex == 1 && validLabel) {
                continue;
            }

            /*
            if (label == null) {
                throw new SyntaxException("Label ");
            }
            if (!label.matches("[A-Z][A-Z0-9]*")) {
                throw new SyntaxException("Label at line " + i + " should be all Uppercase, Alphanumeric, and start with a Letter ");
            }
            labelsMap.put(label, null);

        }
        return null;

             */

        }

        return false;
    }

    private static class ScriptSyntax {
        private final static Map<String, Integer> COMMANDS = new HashMap<>(){};

    }

}
