package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.exceptions.parser.SyntaxException;
import net.crusadergames.bugwars.parser.Parser;
import org.springframework.stereotype.Service;

import java.util.*;

// Technically need to be under Service
// OR
// Convert to a ParserUtil
@Service
public class ParserService {


    public static boolean validateScript(String scriptBody){
        Parser parser = new Parser(scriptBody);
        return parser.isValidScript();
    }

}
