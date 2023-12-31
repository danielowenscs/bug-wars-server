package net.crusadergames.bugwars.repository.script;

import net.crusadergames.bugwars.model.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<Script,Long> {
}
