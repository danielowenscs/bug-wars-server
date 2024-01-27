package net.crusadergames.bugwars.parser;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class Tokenizer {

    public static List<String> tokens;
    private String scriptBody;

    public static void main(String[] args) {
        String testScript = "    :START  att\n" +
                "        att\n" +
                "      att\n" +
                "      GOTO EAT\n" +
                "        att\n" +
                "        att\n" +
                "        att\n" +
                "        att\n" +
                "    :EAT  eat\n" +
                "      GOTO START\n" +
                "     mov\n";
         new Tokenizer(testScript);
         Parser parser = new Parser();
        System.out.println(parser.parseTokens(tokens));
    }

    public Tokenizer(String scriptBody) {
        tokens = new ArrayList<>();
        this.scriptBody = scriptBody;
        tokenize();
    }

    private List<String> tokenize() {
        List<String> lines = new ArrayList<String>(Arrays.asList(scriptBody.split("\\n")));
        // returns list of Tokens
        for (String line : lines) {

            if (line == null || line.trim().equals("") || line.trim().startsWith("#")) {
                continue;
            }

            line = trimComment(line);

            String[] lineTokens = line.trim().split("\\s+");
            tokens.addAll(Arrays.asList(lineTokens));
        }

        return tokens;
    }

    public static String trimComment(String line) {
        if (line.contains("#")) {
            line = line.substring(0, line.indexOf("#"));
        }
        return line;
    }
}
