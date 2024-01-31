package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParserService {

    public static void main(String[] args) {
        String script = "    :START  attack\n" +
                "     moveForward\n" +
                "    GOTO DOBBY\n" +
                ":DOBBY  eat \n";
        validateScript(script);
    }

    public static boolean validateScript(String scriptBody) {


        List<String> lines = new ArrayList<String>(Arrays.asList(scriptBody.split("\\n")));
        Map<String, Boolean> declaredLabels = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) {
                continue;
            }

            line = ScriptSyntax.trimComment(line);

            if (line.isEmpty()) {
                continue;
            }

            final String[] expressions = ScriptSyntax.getExpression(line);
            final int expressionsLength = expressions.length;

            if (expressions.length < ScriptSyntax.MIN_EXPRESSIONS) {
                continue;
            }

            if (expressions.length > ScriptSyntax.MAX_EXPRESSIONS) {
                throw new SyntaxException("Too many arguments on line " + i);
            }


            int keywordIndex = 0;

            if (expressionsLength == ScriptSyntax.MAX_EXPRESSIONS) {
                keywordIndex = 1;
            }

            boolean validLabel = false;
            if (ScriptSyntax.isValidLabel(expressions[0])) {
                if (declaredLabels.containsKey(expressions[0])) {
                    if (declaredLabels.get(expressions[0])) {
                        throw new SyntaxException("Label at line " + " is duplicate");
                    }

                    throw new SyntaxException("Label at line " + i + " is duplicate");
                }
                declaredLabels.put(expressions[0].substring(1), true);
                validLabel = true;
            }

            if (expressionsLength == ScriptSyntax.MAX_EXPRESSIONS && !validLabel) {
                throw new SyntaxException("Invalid label on line " + i);
            }

            if (expressionsLength == ScriptSyntax.MIN_EXPRESSIONS && validLabel) {
                continue;
            }
            final String[] parsedKeywords = ScriptSyntax.parseKeyword(expressions[keywordIndex]);
            if(parsedKeywords == null) {
                throw new SyntaxException("Invalid Expression: " + expressions[keywordIndex]);
            }
            if(parsedKeywords.length == ScriptSyntax.MAX_EXPRESSIONS) {
                if(declaredLabels.containsKey(parsedKeywords[1])) {

                }
            }
        }

        return true;
    }

    private final static class ScriptSyntax {
        public static final int MIN_EXPRESSIONS = 1;
        public static final int MAX_EXPRESSIONS = 2;
        public static final int MIN_BRANCH_CODE = 30;
        private final static Map<String, Integer> keywordMap = new HashMap<String, Integer>() {{
            put("noop", 0);
            put("mov", 10);
            put("rotr", 11);
            put("rotl", 12);
            put("att", 13);
            put("eat", 14);
            put("ifEnemy", 30);
            put("ifAlly", 31);
            put("ifFood", 32);
            put("ifEmpty", 33);
            put("ifWall", 34);
            put("goto", 35);
        }};

        public static Boolean isValidKeyword(String keyword) {
            return keywordMap.containsKey(keyword);
        }

        public static Integer getOpCode(String keyword) {
            return keywordMap.get(keyword);
        }

        public static Boolean isValidLabel(String label) {
            return label.matches("[:][A-Z][A-Z0-9]*");
        }

        public static String trimComment(String line) {
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));
            }
            return line;
        }

        public static String[] getExpression(String line) {
            return line.split("\\r+");
        }

        public static String[] parseKeyword(String keyword) {
            /*
            :LAME goto LAME
                mov START
                ifFood EAT
                att
                ifWall

             */
            String[] keywords = keyword.split(" ");
            if(keywords.length < ScriptSyntax.MIN_EXPRESSIONS || keywords.length > ScriptSyntax.MAX_EXPRESSIONS) {
                return null;
            }
            if(!keywordMap.containsKey(keywords[0])) {
                return null;
            }
            if(keywords.length == ScriptSyntax.MAX_EXPRESSIONS) {
                int opCode = keywordMap.get(keywords[0]);
                if(opCode < MIN_BRANCH_CODE) {
                    return null;
                }
                if(!isValidLabel(":" + keywords[1])) {
                    return null;
                }
            }
            if(keywords.length == ScriptSyntax.MIN_EXPRESSIONS) {
                int opCode = keywordMap.get(keywords[0]);
                if(opCode >= MIN_BRANCH_CODE) {
                    return null;
                }
            }

            return keywords;
        }

    }

}
