package net.crusadergames.bugwars.repository.script;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScriptRepository extends JpaRepository<Script,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM scripts WHERE owner_id = :owner_id")
    List<Script> findByOwner_Id(@Param("owner_id") Long owner_id);

}
