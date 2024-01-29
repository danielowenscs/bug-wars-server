package net.crusadergames.bugwars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_maps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 25)
    @Column(unique = true)
    private String name;

    @NotNull
    private Long height;

    @NotNull
    private Long width;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String body;

}
