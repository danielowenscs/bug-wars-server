package net.crusadergames.bugwars.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    @Column(name = "script_id")
    private Long scriptId;

    @NotBlank
    @Size(max = 25)
    private String name;

    @NotBlank
    private String body;

    @NotNull
    @Column(name = "date_created")
    private LocalDate dateCreated;

    @NotNull
    @Column(name = "date_updated")
    private LocalDate dateUpdated;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    private User user;



}
