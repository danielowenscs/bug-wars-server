package net.crusadergames.bugwars.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ScriptResponse {

    private Long script_id;
    private String script_name;
    private String body;
    private LocalDate date_created;
    private LocalDate date_Updated;
    private Long owner_id;
}
