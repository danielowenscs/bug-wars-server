package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import net.crusadergames.bugwars.parser.maybe.Token;
import net.crusadergames.bugwars.parser.maybe.TokenTypes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Parser {

    private final List<Integer> bytecode;
    private final Map<String,Integer> definedLabels = new HashMap<>();
    public static class UndefinedLabel {
        public String undefinedLabel;
        public int locationInBytecode;
        public UndefinedLabel(String undefinedLabel, int locationInBytecode) {
            this.undefinedLabel = undefinedLabel;
            this.locationInBytecode = locationInBytecode;
        }
    }
    private final List<UndefinedLabel> undefinedLabels = new ArrayList<>();


    private final Map<String, Integer> actionCommands = Commands.getActionCommands();
    private final Map<String, Integer> conditionalCommands = Commands.getConditionalCommands();
    private final Tokenizer tokenizer;
    private boolean isValidScript = false;

    public Parser(String scriptBody) {
        this.tokenizer = new Tokenizer(scriptBody);
        this.bytecode = parseScript();
        this.isValidScript = true;
    }
    public List<Integer> getByteCode(){
        return this.bytecode;
    }

    public boolean isValidScript() {
        return this.isValidScript;
    }

    private List<Integer> parseScript() {
        List<Integer> bytecode = new ArrayList<>();
        int lineNumber = 1;
        while(tokenizer.hasNextToken()) {
            Token currentToken = tokenizer.peekNextToken();
            if(currentToken.getTokenType().equals(TokenTypes.COLON)){
                tokenizer.getNextToken();
                processDefineLabel(bytecode, lineNumber);
            } else if(currentToken.getTokenType().equals(TokenTypes.ACTION)){
                processExpression(bytecode, lineNumber, tokenizer.getNextToken());
            } else if(currentToken.getTokenType().equals(TokenTypes.CONDITIONAL)){
                processExpression(bytecode, lineNumber, tokenizer.getNextToken());
            } else if(currentToken.getTokenType().equals(TokenTypes.WHITESPACE)){
                tokenizer.getNextToken();
            }else if (currentToken.getTokenType().equals(TokenTypes.INVALID_TOKEN)) {
                throw new SyntaxException("Invalid syntax: " + currentToken.getValue() + " at line " + lineNumber);
            }else if(currentToken.getTokenType().equals(TokenTypes.NEWLINE)) {
                tokenizer.getNextToken();
                lineNumber++;
            }else if(currentToken.getTokenType().equals(TokenTypes.COMMENT)) {
                tokenizer.getNextToken();
            }
        }

        for(UndefinedLabel undefinedLabelPair : undefinedLabels) {
            if(!definedLabels.containsKey(undefinedLabelPair.undefinedLabel)) {
                throw new SyntaxException("Label: " + undefinedLabelPair.undefinedLabel + " was undefined");
            }
            bytecode.set(undefinedLabelPair.locationInBytecode, definedLabels.get(undefinedLabelPair.undefinedLabel));
        }
        return bytecode;
    }

    private boolean hasExpressionAfterWhitespace() {
        boolean isEnd = !tokenizer.hasNextToken();
        boolean hasExpression = false;
        while(!isEnd && !hasExpression) {
            Token nextToken = tokenizer.peekNextToken();
            if(nextToken.getTokenType().equals(TokenTypes.COMMENT) || nextToken.getTokenType().equals(TokenTypes.WHITESPACE)) {
                tokenizer.getNextToken();
                isEnd = !tokenizer.hasNextToken();
            } else {
                hasExpression = true;
            }
        }
        return hasExpression;
    }

    private void processDefineLabel(List<Integer> bytecode,int lineNumber){
        if(!tokenizer.hasNextToken()) {
            throw new SyntaxException("Expected label on line: " + lineNumber);
        }
        Token currentToken = tokenizer.getNextToken();
        if(currentToken.getTokenType() != TokenTypes.LABEL) {
            throw new SyntaxException("Labels have to be alphanumerical, and start with a letter" + " at line " + lineNumber);
        }

        String label = currentToken.getValue();
        if(definedLabels.containsKey(label)){
            throw new SyntaxException("Cannot define the same label twice :" + label + " at line " + lineNumber);
        }
        definedLabels.put(label, bytecode.size());
        if(hasExpressionAfterWhitespace()) {
            if(!tokenizer.peekNextToken().getTokenType().equals(TokenTypes.NEWLINE)) {
                processExpression(bytecode,lineNumber,tokenizer.getNextToken());
            }
        }
    }
    private void processExpression(List<Integer> bytecode, int lineNumber,Token currentToken){
        if (currentToken.getTokenType().equals(TokenTypes.ACTION)) {
                bytecode.add(actionCommands.get(currentToken.getValue()));
                if(hasExpressionAfterWhitespace()) {
                    if(!tokenizer.peekNextToken().getTokenType().equals(TokenTypes.NEWLINE)) {
                        throw new SyntaxException("Only one operation per line at line " + lineNumber);
                    }
                }
            } else if (currentToken.getTokenType().equals(TokenTypes.CONDITIONAL)) {
                bytecode.add(conditionalCommands.get(currentToken.getValue()));
                if (!tokenizer.hasNextToken()) {
                    throw new SyntaxException("Expected whitespace after the conditional at line " + lineNumber);
                }
                Token nextToken = tokenizer.getNextToken();
                if (!nextToken.getTokenType().equals(TokenTypes.WHITESPACE)) {
                    throw new SyntaxException("Expected whitespace after the conditional at line " + lineNumber);
                }
                nextToken = tokenizer.getNextToken();
                String label = nextToken.getValue();
                if (definedLabels.containsKey(label)) {
                    bytecode.add(definedLabels.get(label));
                } else {
                    undefinedLabels.add(new UndefinedLabel(label, bytecode.size()));
                    bytecode.add(0);
                }
                if(hasExpressionAfterWhitespace()) {
                    if(!tokenizer.peekNextToken().getTokenType().equals(TokenTypes.NEWLINE)) {
                        throw new SyntaxException("Only one operation per line at line " + lineNumber);
                    }
                }
            } else {
                throw new SyntaxException("Expected action or conditional at line " + lineNumber);
            }
    }
}
