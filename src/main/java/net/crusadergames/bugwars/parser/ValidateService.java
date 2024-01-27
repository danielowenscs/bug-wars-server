package net.crusadergames.bugwars.parser;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ValidateService {

    public static boolean validateScript(String scriptBody){
        List<String> lines = new ArrayList<String>(Arrays.asList(scriptBody.split("\\n")));
        return true;
    }
}
