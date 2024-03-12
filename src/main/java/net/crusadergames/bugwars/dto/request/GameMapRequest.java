package net.crusadergames.bugwars.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMapRequest {

    @Size(max = 25)
    private String name;

    private Long terrainId;

    private int height;

    private int width;

    private String body;

    private String imagePath;

}
