package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.iss.Komsiluk.beans.BlockNote;

@Repository
public interface BlockNoteRepository extends JpaRepository<BlockNote, Long> {

    public List<BlockNote> findByBlockedUserId(Long userId);
}
