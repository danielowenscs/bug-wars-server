package net.crusadergames.bugwars.repository.script;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScriptRepository extends JpaRepository<Script,Long> {
    List<Script> getScriptsByUser(User user);
    List<Script> findScriptsByName(User user);
    Optional<Script> findScriptByName(String name);
}
