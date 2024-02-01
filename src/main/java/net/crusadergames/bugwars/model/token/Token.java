package net.crusadergames.bugwars.model.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {

    private TokenTypes tokenType;

    private String value;

}
