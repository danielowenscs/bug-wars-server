package net.crusadergames.bugwars.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.model.auth.User;
import java.time.LocalDate;

@Entity
@Table(name = "scripts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long script_id;

    @NotBlank
    @Size(max = 25)
    private String script_name;

    @NotBlank
    private String body;

    @NotNull
    private LocalDate date_created;

    @NotNull
    private LocalDate date_Updated;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;


}
