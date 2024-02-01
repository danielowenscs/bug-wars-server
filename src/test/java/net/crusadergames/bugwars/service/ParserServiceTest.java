package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.Util.Constants;
import net.crusadergames.bugwars.exceptions.ScriptNameAlreadyExistsException;
import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import net.crusadergames.bugwars.parser.Commands;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserServiceTest {
    private final ParserService parserService = new ParserService();
    @Test
    void returnByteCode_ShouldReturnEmptyListIfScriptEmpty(){
        String script = "";
        List<Integer> actualBytecode = parserService.returnByteCode(script);
        List<Integer> expectedBytecode = new ArrayList<>();
        assertEquals(actualBytecode, expectedBytecode);
    }

    @Test
    void returnByteCode_ShouldReturnCorrectByteCodeForEachAction() {
        String allActions = "mov\n" +
                "rotr\n" +
                "rotl\n" +
                "att\n" +
                "eat\n";
        List<Integer> actualBytecode = parserService.returnByteCode(allActions);
        List<Integer> expectedBytecode = new ArrayList<>(List.of(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_MOV),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTR),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTL),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_EAT)));
        assertEquals(actualBytecode, expectedBytecode);
    }
    @Test
    void returnByteCode_ShouldReturnCorrectByteCodeForEachActionDespiteLeadingWhitespace() {
        String allActions = " mov\n" +
                "\trotr\n" +
                " \t rotl\n" +
                "\t\tatt\n" +
                "\n" +
                "eat\n";
        List<Integer> actualBytecode = parserService.returnByteCode(allActions);
        List<Integer> expectedBytecode = new ArrayList<>(List.of(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_MOV),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTR),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTL),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_EAT)));
        assertEquals(actualBytecode, expectedBytecode);
    }
    @Test
    void returnByteCode_ShouldReturnCorrectByteCodeForEachActionDespiteTrailingWhitespace() {
        String allActions = "mov \n" +
                "rotr\t\n" +
                "rotl \t\n" +
                "att\t\t\n" +
                "\n" +
                "eat\n\n";
        List<Integer> actualBytecode = parserService.returnByteCode(allActions);
        List<Integer> expectedBytecode = new ArrayList<>(List.of(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_MOV),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTR),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ROTL),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_EAT)));
        assertEquals(actualBytecode, expectedBytecode);
    }

    @Test
    void returnByteCode_ShouldReturnCorrectByteCodeDespiteTrailingComment() {
        String allActions = "mov#Move\n";
        List<Integer> actualBytecode = parserService.returnByteCode(allActions);
        List<Integer> expectedBytecode = new ArrayList<>();
        expectedBytecode.add(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_MOV));
        assertEquals(actualBytecode, expectedBytecode);
    }

    @Test
    void returnByteCode_ShouldReturnCorrectByteCodeForEachConditional(){
        String allConditionals = ":START\tatt\n" +
                "\tifEnemy START\n" +
                "\tifAlly START\n" +
                "\tifFood START\n" +
                "\tifEmpty START\n" +
                "\tifWall START\n" +
                "\tgoto START";
        List<Integer> actualBytecode = parserService.returnByteCode(allConditionals);
        List<Integer> expectedBytecode = new ArrayList<>(List.of(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_IFENEMY),0,
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_IFALLY),0,
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_IFFOOD),0,
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_IFEMPTY),0,
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_IFWALL),0,
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_GOTO),0));
        assertEquals(actualBytecode, expectedBytecode);
    }

    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenInvalidAction(){
        Exception exception = Assert.assertThrows(SyntaxException.class, () ->{
            String script = "ata";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
        String expectedErrorMessage = "Invalid syntax: a at line 1";
        assertEquals(expectedErrorMessage,exception.getMessage());
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenMultipleActionsOneLine(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = "att att att";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenLabelDefinedTwice(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    ":START\tatt";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenLabelNotDefined(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    "\tgoto STAR";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }

    @Test
    void returnByteCode_shouldBeAbleToUseLabelDefinedLater(){
        String script = ":START\tatt\n" +
                "\tgoto END\n" +
                "\tatt\n" +
                ":END\tmov";
        List<Integer> actualBytecode = parserService.returnByteCode(script);
        List<Integer> expectedBytecode = new ArrayList<>(List.of(Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getConditionalCommands().get(Constants.CONDITIONALCOMMAND_GOTO),4,
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_ATT),
                Commands.getActionCommands().get(Constants.ACTIONCOMMAND_MOV)));
        assertEquals(actualBytecode, expectedBytecode);

    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenNoLabelToDefine(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":\tatt\n";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenEndOfScriptDuringDefineLabel(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenScriptEndsAfterConditional(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    "\tifWall";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenNoSpaceAfterConditional(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    "\tifWallSTART";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenNoLabelAfterConditional(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    "\tifWall rotl";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenActionOccursAfterConditional(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tatt\n" +
                    "\tifWall START rotl";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenDefiningLabelWithBadSyntax(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tbad\n" +
                    "\tifWall START";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
    @Test
    void returnByteCode_shouldThrowSyntaxExceptionWhenConditionalWrongValue(){
        Assert.assertThrows(SyntaxException.class, () ->{
            String script = ":START\tbad\n" +
                    "\tifWbll START";
            List<Integer> actualBytecode = parserService.returnByteCode(script);
        });
    }
}