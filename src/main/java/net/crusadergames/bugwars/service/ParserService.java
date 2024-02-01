package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import net.crusadergames.bugwars.model.token.UndefinedLabel;
import net.crusadergames.bugwars.parser.Commands;
import net.crusadergames.bugwars.parser.Tokenizer;
import net.crusadergames.bugwars.model.token.Token;
import net.crusadergames.bugwars.model.token.TokenTypes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParserService {

    private final Map<String,Integer> definedLabels = new HashMap<>();
    private final List<UndefinedLabel> undefinedLabels = new ArrayList<>();

    public List<Integer> returnByteCode(String scriptBody){
        return parseScript(scriptBody);
    }

    private List<Integer> parseScript(String scriptBody) {
        List<Integer> bytecode = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer(scriptBody);
        int lineNumber = 1;
        while(tokenizer.hasNextToken()) {
            Token currentToken = tokenizer.peekNextToken();

            if(currentToken.getTokenType().equals(TokenTypes.COLON)){
                tokenizer.getNextToken();
                processDefineLabel(bytecode, lineNumber, tokenizer);
            } else if((currentToken.getTokenType().equals(TokenTypes.ACTION)) || (currentToken.getTokenType().equals(TokenTypes.CONDITIONAL))){
                processExpression(bytecode, lineNumber, tokenizer.getNextToken(), tokenizer);
            } else if((currentToken.getTokenType().equals(TokenTypes.WHITESPACE)) || (currentToken.getTokenType().equals(TokenTypes.COMMENT))){
                tokenizer.getNextToken();
            } else if(currentToken.getTokenType().equals(TokenTypes.NEWLINE)) {
                tokenizer.getNextToken();
                lineNumber++;
            } else if (currentToken.getTokenType().equals(TokenTypes.INVALID_TOKEN)) {
                throw new SyntaxException("Invalid syntax: " + currentToken.getValue() + " at line " + lineNumber);
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

    private boolean hasExpressionAfterWhitespace(Tokenizer tokenizer) {
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

    private void processDefineLabel(List<Integer> bytecode, int lineNumber, Tokenizer tokenizer){
        if(!tokenizer.hasNextToken()) {
            throw new SyntaxException("Expected label on line: " + lineNumber);
        }
        Token currentToken = tokenizer.getNextToken();
        if(currentToken.getTokenType() != TokenTypes.LABEL) {
            throw new SyntaxException("Labels have to be alphanumerical, and start with a letter at line " + lineNumber);
        }

        String label = currentToken.getValue();
        if(definedLabels.containsKey(label)){
            throw new SyntaxException("Cannot define the same label twice: " + label + " at line " + lineNumber);
        }
        definedLabels.put(label, bytecode.size());
        if(hasExpressionAfterWhitespace(tokenizer)) {
            if(!tokenizer.peekNextToken().getTokenType().equals(TokenTypes.NEWLINE)) {
                processExpression(bytecode, lineNumber, tokenizer.getNextToken(), tokenizer);
            }
        }
    }

    private void processExpression(List<Integer> bytecode, int lineNumber, Token currentToken, Tokenizer tokenizer){
        Map<String, Integer> actionCommands = Commands.getActionCommands();
        Map<String, Integer> conditionalCommands = Commands.getConditionalCommands();

        if (currentToken.getTokenType().equals(TokenTypes.ACTION)) {
                bytecode.add(actionCommands.get(currentToken.getValue()));
                if(hasExpressionAfterWhitespace(tokenizer)) {
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
                if(!nextToken.getTokenType().equals(TokenTypes.LABEL)) {
                    throw new SyntaxException("Expected label at line " + lineNumber);
                }
                String label = nextToken.getValue();
                if (definedLabels.containsKey(label)) {
                    bytecode.add(definedLabels.get(label));
                } else {
                    undefinedLabels.add(new UndefinedLabel(label, bytecode.size()));
                    bytecode.add(0);
                }
                if(hasExpressionAfterWhitespace(tokenizer)) {
                    if(!tokenizer.peekNextToken().getTokenType().equals(TokenTypes.NEWLINE)) {
                        throw new SyntaxException("Only one operation per line at line " + lineNumber);
                    }
                }
            } else {
                throw new SyntaxException("Expected action or conditional at line " + lineNumber);
            }
    }
}
