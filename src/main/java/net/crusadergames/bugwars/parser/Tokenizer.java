package net.crusadergames.bugwars.parser;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class Tokenizer {

    private List<Token> tokens;
    private String scriptBody;


    public Tokenizer(String scriptBody) {
        String testScript = "    :START  attack\n" +
                "     moveForward\n" +
                "    GOTO DOBBY\n" +
                ":DOBBY  eat \n";
        this.scriptBody = testScript;
        tokenize();
    }

    private List<Token> tokenize() {
        List<String> lines = new ArrayList<String>(Arrays.asList(scriptBody.split("\\n")));
        // returns list of Tokens
        for (String line : lines) {

            if (line == null || line.trim().equals("") || line.trim().startsWith("#")) {
                row++;
                continue;
            }

            if (line.contains("#")){
                Integer hashIndex = line.indexOf("#");
                line = line.substring(0, hashIndex);
            }

            String[] tokens = line.trim().split("\\s+");
            LineOfTokens temp = new LineOfTokens(row, Arrays.asList(tokens));
            result.add(temp);

        }

        return tokens;
    }

    public List<Token> tokenizeLine(String line) {
        List<Token> lineTokens = new ArrayList<>();
        String tokenValue;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == ':') {
                lineTokens.add(new Token(TokenTypes.COLON, ":"));
            }

            line = trimComment(line);
        }
    }


    public static String trimComment(String line) {
        if (line.contains("#")) {
            line = line.substring(0, line.indexOf("#"));
        }
        return line;
    }
}
