package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.BlockNote;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.BlockNoteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.BlockNoteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IBlockNoteService;

@Service
public class BlockNoteService implements IBlockNoteService {

	@Autowired
    private BlockNoteRepository blockNoteRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private BlockNoteDTOMapper mapper;

    @Override
    public BlockNoteResponseDTO createBlock(BlockNoteCreateDTO dto) {
    	
        User blocked = userRepository.findByEmailIgnoreCase(dto.getBlockedUserEmail());
        if (blocked == null) {
			throw new NotFoundException("Blocked user not found");
		}
        
        User admin = userRepository.findById(dto.getAdminId()).orElseThrow(() -> new NotFoundException("Admin user not found"));

        blocked.setBlocked(true);
        userRepository.save(blocked);

        BlockNote note = new BlockNote();
        note.setBlockedUser(blocked);
        note.setAdmin(admin);
        note.setReason(dto.getReason());
        note.setCreatedAt(LocalDateTime.now());

        note = blockNoteRepository.save(note);

        return mapper.toResponseDTO(note);
    }

    @Override
    public BlockNoteResponseDTO getLastBlockForUser(Long userId) {
        return blockNoteRepository.findByBlockedUserId(userId).stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).findFirst().map(mapper::toResponseDTO).orElse(null);
    }
}