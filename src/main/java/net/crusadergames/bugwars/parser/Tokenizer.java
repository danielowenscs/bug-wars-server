package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.model.token.Token;
import net.crusadergames.bugwars.model.token.TokenTypes;

import java.util.*;


public class Tokenizer {
    private final static int OPERATION_MIN_NONROTATE_SIZE = 3;
    private final static int OPERATION_MIN_ROTATE_SIZE = 4;
    private final static int CONDITIONAL_MIN_SIZE = 6;
    private final static int CONDITIONAL_MAX_SIZE = 7;
    private final static int UNIQUE_CONDTIONAL_INDEX = 2;
    private final static int GOTO_SIZE = 4;

    public final static Set<String> VALID_OPERATIONS = new HashSet<>(Arrays.asList("att", "mov", "eat","rotl","rotr"));
    public final static Set<String> VALID_CONDITIONALS = new HashSet<>(Arrays.asList("ifEnemy","ifAlly","ifFood","ifEmpty","ifWall","goto"));

    public static List<String> tokens;
    private final List<Token> tokensList;
    private int currentToken;
    private final String scriptBody;

//    public static void main(String[] args) {
//        String testScript = "#Test\n" +
//                ":START  eat\n" +
//                "att\n" +
//                "mov\n" +
//                "att\n" +
//                "   ifFood EAT\n" +
//                "        att\n" +
//                "      att\n" +
//                "      goto EAT\n" +
//                "        att\n" +
//                "        att\n" +
//                "        att\n" +
//                "        att\n" +
//                ":EAT  eat\n" +
//                "      goto START\n" +
//                "     mov\n";
//         new Tokenizer(testScript);
//         Parser parser = new Parser(testScript);
//         System.out.println(parser.returnByteCode().toString());
//    }

    public Tokenizer(String scriptBody) {
        this.scriptBody = scriptBody;
        this.tokensList = generateTokens();
        this.currentToken = 0;
    }
    public boolean hasNextToken(){
        return currentToken < tokensList.size();
    }
    public Token getNextToken(){
        Token nextToken = hasNextToken() ? tokensList.get(currentToken) : null;
        currentToken++;
        return nextToken;
    }
    public Token peekNextToken(){
        Token nextToken = hasNextToken() ? tokensList.get(currentToken) : null;
        return nextToken;
    }
    private List<Token> generateTokens() {
        List<Token> tokens = new ArrayList<>();
        int currentScriptIndex = 0;
        while(currentScriptIndex < scriptBody.length()) {
            Token nextToken = createNextToken(currentScriptIndex);
            tokens.add(nextToken);
            currentScriptIndex = currentScriptIndex + nextToken.getValue().length();
        }
        return tokens;
    }
    private Token createNextToken(int streamIndex) {
        Token nextToken = null;
        Character firstCharacter = scriptBody.charAt(streamIndex);
        if(scriptBody.charAt(streamIndex) == ':') {
            nextToken = new Token(TokenTypes.COLON,":");            
        } else if (isCapitalLetter(firstCharacter)) {
            nextToken = createLabelToken(streamIndex);
        } else if (isStartOfOperation(firstCharacter)) {
            nextToken = createOperationToken(streamIndex);
        } else if(isStartOfConditional(firstCharacter)) {
            nextToken = createConditionalToken(streamIndex);
        } else if(isComment(firstCharacter)) {
            nextToken = createCommentToken(streamIndex);
        } else if(isWhiteSpace(firstCharacter)) {
            nextToken = createWhiteSpaceToken(streamIndex);
        } else if(firstCharacter == '\n') {
            nextToken = new Token(TokenTypes.NEWLINE,"\n");
        } else {
            nextToken = new Token(TokenTypes.INVALID_TOKEN,String.valueOf(firstCharacter));
        }
        return nextToken;
    }
    private Token createLabelToken(int streamIndex){
        int endIndex = streamIndex + 1;
        boolean stillHaveValidLabel = true;
        while(endIndex < scriptBody.length() && stillHaveValidLabel) {
            if((isCapitalLetter(scriptBody.charAt(endIndex))) || isDigit(scriptBody.charAt(endIndex))) {
                endIndex++;
            } else {
                stillHaveValidLabel = false;
            }
        }
        return new Token(TokenTypes.LABEL,scriptBody.substring(streamIndex,endIndex));
    }

    private Token createOperationToken(int streamIndex) {
        String currentTokenValue = String.valueOf(scriptBody.charAt(streamIndex));
        TokenTypes currentTokenType = TokenTypes.INVALID_TOKEN;
        int operationLength = scriptBody.charAt(streamIndex) != 'r' ? OPERATION_MIN_NONROTATE_SIZE : OPERATION_MIN_ROTATE_SIZE;
        int endIndex = streamIndex + operationLength;
        boolean isValidOperation = endIndex <= scriptBody.length();
        if(isValidOperation) {
            if(VALID_OPERATIONS.contains(scriptBody.substring(streamIndex,endIndex))){
                currentTokenValue = scriptBody.substring(streamIndex,endIndex);
                currentTokenType = TokenTypes.ACTION;
            }
        }
        return new Token(currentTokenType,currentTokenValue);
    }

    private Token createConditionalToken(int streamIndex){
        String currentTokenValue = String.valueOf(scriptBody.charAt(streamIndex));
        TokenTypes currentTokenType = TokenTypes.INVALID_TOKEN;
        int conditionalLength = scriptBody.charAt(streamIndex) == 'g' ? GOTO_SIZE : UNIQUE_CONDTIONAL_INDEX;
        int endIndex = streamIndex + conditionalLength;
        boolean isValidConditional = endIndex <= scriptBody.length();
        if(isValidConditional && scriptBody.charAt(streamIndex) != 'g') {
            conditionalLength = scriptBody.charAt(endIndex) == 'E' ? CONDITIONAL_MAX_SIZE : CONDITIONAL_MIN_SIZE;
            endIndex = streamIndex + conditionalLength;
            isValidConditional = endIndex <= scriptBody.length();
        }
        if(isValidConditional) {
            if(VALID_CONDITIONALS.contains(scriptBody.substring(streamIndex,endIndex))) {
                currentTokenValue = scriptBody.substring(streamIndex,endIndex);
                currentTokenType = TokenTypes.CONDITIONAL;
            } else {
                currentTokenValue = scriptBody.substring(streamIndex,endIndex);
            }
        }
        return new Token(currentTokenType,currentTokenValue);
    }
    private Token createCommentToken(int streamIndex) {
        int endIndex = streamIndex + 1;
        boolean needNewLine = true;
        while(endIndex < scriptBody.length() && needNewLine) {
            if(scriptBody.charAt(endIndex) != '\n'){
                endIndex++;
            } else {
                needNewLine = false;
            }
        }
        return new Token(TokenTypes.COMMENT,scriptBody.substring(streamIndex,endIndex));
    }
    private Token createWhiteSpaceToken(int streamIndex) {
        int endIndex = streamIndex + 1;
        boolean needNonWhiteSpace = true;
        while(endIndex < scriptBody.length() && needNonWhiteSpace) {
            if(isWhiteSpace(scriptBody.charAt(endIndex))) {
                endIndex++;
            } else {
                needNonWhiteSpace = false;
            }
        }
        return new Token(TokenTypes.WHITESPACE,scriptBody.substring(streamIndex,endIndex));
    }

    private boolean isCapitalLetter(Character character) {
        return character >= 'A' && character <= 'Z';
    }
    private boolean isDigit(Character character) {
        return character >= '0' && character <= '9';
    }
    private boolean isStartOfOperation(Character character) {
        return character == 'm' || character == 'a' || character == 'e' || character == 'r';
    }
    private boolean isStartOfConditional(Character character) {
        return character == 'i' || character == 'g';
    }
    private boolean isComment(Character character) {
        return character == '#';
    }
    private boolean isWhiteSpace(Character character) {
        return character == ' ' || character == '\t';
    }
}
