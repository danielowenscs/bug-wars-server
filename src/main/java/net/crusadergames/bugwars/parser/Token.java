package net.crusadergames.bugwars.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private TokenTypes tokenType;

    private String value;

}
