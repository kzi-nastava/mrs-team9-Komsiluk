package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.BlockNote;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.BlockNoteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.BlockNoteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
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
    	
        User blocked = userRepository.findByEmail(dto.getBlockedUserEmail());

        User admin = userRepository.findById(dto.getAdminId());

        blocked.setBlocked(true);
        userRepository.save(blocked);

        BlockNote node = new BlockNote();
        node.setBlockedUser(blocked);
        node.setAdmin(admin);
        node.setReason(dto.getReason());
        node.setCreatedAt(LocalDateTime.now());

        node = blockNoteRepository.save(node);

        return mapper.toResponseDTO(node);
    }

    @Override
    public BlockNoteResponseDTO getLastBlockForUser(Long userId) {
        List<BlockNote> list = blockNoteRepository.findByBlockedUserId(userId);

        return list.stream().max(Comparator.comparing(BlockNote::getCreatedAt)).map(mapper::toResponseDTO).orElse(null);
    }
}