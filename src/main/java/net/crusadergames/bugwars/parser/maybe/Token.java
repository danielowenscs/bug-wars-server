package net.crusadergames.bugwars.parser.maybe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.parser.maybe.TokenTypes;

@Data
@AllArgsConstructor
public class Token {

    private TokenTypes tokenType;

    private String value;

}
