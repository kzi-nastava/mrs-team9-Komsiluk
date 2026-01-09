package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.BlockNote;

@Repository
public class BlockNoteRepository {

    private final Map<Long, BlockNote> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public BlockNote save(BlockNote node) {
        if (node.getId() == null) {
            node.setId(idGen.getAndIncrement());
        }
        storage.put(node.getId(), node);
        return node;
    }

    public List<BlockNote> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<BlockNote> findByBlockedUserId(Long userId) {
        if (userId == null) return Collections.emptyList();

        return storage.values().stream()
                .filter(b -> b.getBlockedUser() != null
                        && userId.equals(b.getBlockedUser().getId()))
                .collect(Collectors.toList());
    }
}
