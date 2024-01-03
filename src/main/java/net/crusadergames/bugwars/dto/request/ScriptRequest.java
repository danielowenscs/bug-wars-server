package net.crusadergames.bugwars.controller;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScriptRequest {

    @Size(max = 25)
    private String script_name;

    private String script_body;

}
